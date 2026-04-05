package cz.anduria.core.managers;

import cz.anduria.core.AnduriaCore;
import java.util.*;

public class AFKManager {
    private final Set<UUID> afk = new HashSet<>();
    public AFKManager(AnduriaCore plugin) {}
    public boolean isAFK(UUID u)            { return afk.contains(u); }
    public void setAFK(UUID u, boolean v)   { if (v) afk.add(u); else afk.remove(u); }
    public void toggleAFK(UUID u)           { setAFK(u, !isAFK(u)); }
}