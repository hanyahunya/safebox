package com.safebox.back.rpi.service;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RpiAccessStatus {
    private final Set<String> activeParcelRpiSet = ConcurrentHashMap.newKeySet();

    public void onParcelArrived(String rpiId) {
        activeParcelRpiSet.add(rpiId);
    }

    public void onParcelRetrieved(String rpiId) {
        activeParcelRpiSet.remove(rpiId);
    }

    public boolean getStatus(String rpiId) {
        return activeParcelRpiSet.contains(rpiId);
    }
}
