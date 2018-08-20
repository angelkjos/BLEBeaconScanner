package com.angelkjoseski.blebeacon;

import android.util.Log;

import java.util.HashMap;
import java.util.Vector;

public class IBeaconManager {
    public interface Listener {
        void onEnter(IBeaconResult data, IBeaconRegion region);

        void onDetect(IBeaconResult data, IBeaconRegion region);

        void onLeave(IBeaconResult data, IBeaconRegion region);

        void onIngored(IBeaconResult data, IBeaconRegion region);
    }

    Listener listener;

    HashMap<String, Node> map = new HashMap<>();
    Node head = null;
    Node end = null;

    public IBeaconManager(Listener listener) {
        this.listener = listener;
    }

    public IBeaconResult get(String key) {
        if (map.containsKey(key)) {
            Node n = map.get(key);
            remove(n);
            setHead(n);
            return n.data;
        }
        return null;
    }

    public Vector<IBeaconResult> getAll(long fromTS) {
        Vector<IBeaconResult> results = new Vector();
        for(Node node : map.values()) {
            if (node.data.timestamp >= fromTS) {
                results.add(node.data);
            }
        }
        return results;
    }

    public void onResult(IBeaconResult data) {
        long now = IBeaconUtils.now();
        onTimer(now);

        String key = IBeaconRegion.createId(data.uuid, data.major, data.minor);
        IBeaconRegion region = IBeaconRegionManager.getMatchRegion(data);
        if (region == null || data.rssi < data.rssi1m + region.powerThreshold) {
            this.listener.onIngored(data, region);
            return;
        }

        long nodeExpiration = data.timestamp + region.ttl;

        if (map.containsKey(key)) {
            Node old = map.get(key);
            old.update(data, region, nodeExpiration);

            remove(old);
            setHead(old);

            this.listener.onDetect(data, region);
        } else {
            Node created = new Node(key, data, region, nodeExpiration);

            map.put(key, created);
            setHead(created);

            this.listener.onEnter(data, region);
        }
    }

    public void clear() {
        end = null;
        head = null;

        HashMap<String, Node> backup = map;
        map = new HashMap<>();

        for (Node node : backup.values()) {
            listener.onLeave(node.data, node.region);
        }
    }

    /* Timer management */

    public void onTimer() {
        long now = IBeaconUtils.now();
        onTimer(now);
    }

    private void onTimer(long now) {
        while (end != null && end.expiration <= now) {
            Node backup = end;

            map.remove(end.key);
            remove(end);

            listener.onLeave(backup.data, backup.region);
        }
    }

    /* Node management */

    private class Node {
        String key;
        IBeaconResult data;
        IBeaconRegion region;
        long expiration;
        Node pre;
        Node next;

        public Node(String key, IBeaconResult data, IBeaconRegion region, long expiration) {
            this.key = key;
            this.data = data;
            this.region = region;
            this.expiration = expiration;
        }

        public void update(IBeaconResult data, IBeaconRegion region, long expiration) {
            this.data = data;
            this.region = region;
            this.expiration = expiration;
        }
    }

    private void remove(Node n) {
        if (n.pre != null) {
            n.pre.next = n.next;
        } else {
            head = n.next;
        }

        if (n.next != null) {
            n.next.pre = n.pre;
        } else {
            end = n.pre;
        }
    }

    private void setHead(Node n) {
        n.next = head;
        n.pre = null;

        if (head != null) {
            head.pre = n;
        }

        head = n;
        if (end == null) {
            end = head;
        }
    }
}