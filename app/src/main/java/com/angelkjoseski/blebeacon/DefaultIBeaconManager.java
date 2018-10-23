package com.angelkjoseski.blebeacon;

/**
 * Listener which just creates and displays a notification.
 */
public class DefaultIBeaconManager implements IBeaconManager.Listener {
    @Override
    public void onEnter(IBeaconResult data, IBeaconRegion region) {
        NotificationHelper.showNotification(region.name + " enter.", 11);
    }

    @Override
    public void onDetect(IBeaconResult data, IBeaconRegion region) {
        NotificationHelper.showNotification(region.name + " detect.", 22);
    }

    @Override
    public void onLeave(IBeaconResult data, IBeaconRegion region) {
        NotificationHelper.showNotification(region.name + " leave.", 33);
    }

    @Override
    public void onIngored(IBeaconResult data, IBeaconRegion region) {
        NotificationHelper.showNotification(region.name + " ignored.", 44);
    }
}
