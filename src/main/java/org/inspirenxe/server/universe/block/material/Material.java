package org.inspirenxe.server.universe.block.material;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;

/**
 *
 */
public abstract class Material {
    private static final TShortObjectMap<MasterMaterial> MATERIALS_BY_ID = new TShortObjectHashMap<>();
    private final short id;
    private final short subID;

    protected Material(short id, short subID) {
        this.id = id;
        this.subID = subID;
    }

    public short getID() {
        return id;
    }

    public short getSubID() {
        return subID;
    }

    protected static void register(MasterMaterial material) {
        final MasterMaterial previous = MATERIALS_BY_ID.put(material.getID(), material);
        if (previous != null) {
            System.out.println("New material has conflicting ID, previous material was overwritten: " + previous + " => " + material);
        }
    }

    public static Material get(short id) {
        return get(id, (short) 0);
    }

    public static Material get(short id, short subID) {
        final MasterMaterial master = MATERIALS_BY_ID.get(id);
        if (master == null) {
            return Materials.AIR;
        }
        if (subID == 0) {
            return master;
        }
        final SubMaterial sub = master.getSubMaterial(subID);
        if (sub == null) {
            return master;
        }
        return sub;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", subID=" + subID +
                '}';
    }
}
