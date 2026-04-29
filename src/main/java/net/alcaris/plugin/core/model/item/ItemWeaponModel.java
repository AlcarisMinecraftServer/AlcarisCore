package net.alcaris.plugin.core.model.item;

import java.util.List;

@SuppressWarnings("unused")
public class ItemWeaponModel {
    private String weapon_type;
    private int required_level;
    private int max_modification;
    private int durability;
    private Base base;

    public static class Base {
        private Attributes attributes;
        private List<Common.Buff> buffs;
        private List<Common.Effect> effects;

        public Attributes getAttributes() {
            return attributes;
        }

        public List<Common.Buff> getBuffs() {
            return buffs;
        }

        public List<Common.Effect> getEffects() {
            return effects;
        }
    }

    public static class Attributes {
        private float attack_damage;
        private float attack_range;
        private float attack_speed;
        private float movement_speed;
        private float mp;
        private float mpr;
        private float atk;
        private float def;
        private float mdf;
        private float crt;
        private float crd;
        private float spd;
        private float luk;
        private float hpr; // HPRを追加

        public float getAttackDamage() {
            return attack_damage;
        }

        public float getAttackRange() {
            return attack_range;
        }

        public float getAttackSpeed() {
            return attack_speed;
        }

        public float getMovementSpeed() {
            return movement_speed;
        }

        public float getHpr() { // HPRのgetterを追加
            return hpr;
        }

        public float getMp() {
            return mp;
        }

        public float getMpr() {
            return mpr;
        }

        public float getAtk() {
            return atk;
        }

        public float getDef() {
            return def;
        }

        public float getMdf() {
            return mdf;
        }

        public float getCrt() {
            return crt;
        }

        public float getCrd() {
            return crd;
        }

        public float getSpd() {
            return spd;
        }

        public float getLuk() {
            return luk;
        }
    }

    public float getDamage() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackDamage() : 0f;
    }

    public float getWalkSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMovementSpeed() : 0f;
    }

    public float getAttackRange() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackRange() : 0f;
    }

    public float getAttackSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackSpeed() : 0f;
    }

    public float getHpr() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getHpr() : 0f;
    }

    public float getMp() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMp() : 0f;
    }

    public float getMpr() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMpr() : 0f;
    }

    public float getAtk() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAtk() : 0f;
    }

    public float getDef() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getDef() : 0f;
    }

    public float getMdf() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMdf() : 0f;
    }

    public float getCrt() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrt() : 0f;
    }

    public float getCrd() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrd() : 0f;
    }

    public float getSpd() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getSpd() : 0f;
    }

    public float getLuk() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getLuk() : 0f;
    }

    public String getWeaponType() {
        return weapon_type;
    }

    public int getRequiredLevel() {
        return required_level;
    }

    public int getMaxModification() {
        return max_modification;
    }

    public int getDurability() {
        return durability;
    }

    public Base getBase() {
        return base;
    }
}