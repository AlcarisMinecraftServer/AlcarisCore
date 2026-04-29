package net.alcaris.plugin.core.model.item;

import java.util.List;

@SuppressWarnings("unused")
public class ItemArmorModel {
    private String armor_type;
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
        private float hp;
        private float hpr;
        private float mp;
        private float mpr;
        private float atk;
        private float def;
        private float mat;
        private float mdf;
        private float dex;
        private float speed;
        private float crt;
        private float crd;
        private float luk;
        private float movement_speed;

        public float getHp() {
            return hp;
        }

        public float getHpr() {
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

        public float getMat() {
            return mat;
        }

        public float getMdf() {
            return mdf;
        }

        public float getDex() {
            return dex;
        }

        public float getSpeed() {
            return speed;
        }

        public float getCrt() {
            return crt;
        }

        public float getCrd() {
            return crd;
        }

        public float getLuk() {
            return luk;
        }

        public float getMovementSpeed() {
            return movement_speed;
        }
    }

    public float getHp() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getHp() : 0f;
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

    public float getMat() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMat() : 0f;
    }

    public float getMdf() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMdf() : 0f;
    }

    public float getDex() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getDex() : 0f;
    }

    public float getSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getSpeed() : 0f;
    }

    public float getCrt() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrt() : 0f;
    }

    public float getCrd() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrd() : 0f;
    }

    public float getLuk() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getLuk() : 0f;
    }

    public float getMovementSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMovementSpeed() : 0f;
    }

    public String getArmorType() {
        return armor_type;
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