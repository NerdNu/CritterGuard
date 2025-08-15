package me.ppgome.mountGuard;

import me.ppgome.mountGuard.database.MountAccess;
import me.ppgome.mountGuard.database.SavedMount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MountCache {

    private HashMap<UUID, SavedMount> cachedMounts;
    private HashMap<UUID, ArrayList<MountAccess>> cachedPlayers;

}
