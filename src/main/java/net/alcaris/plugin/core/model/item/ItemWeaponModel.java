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
        private List<Buff> buffs;
        private List<Effect> effects;

        public Attributes getAttributes() {
            return attributes;
        }

        public List<Buff> getBuffs() {
            return buffs;
        }

        public List<Effect> getEffects() {
            return effects;
        }
    }

    public static class Attributes {
        private double attack_damage;
        private double attack_range;
        private double attack_speed;
        private double movement_speed;
        private double mp;
        private double mpr;
        private double atk;
        private double def;
        private double mdf;
        private double crt;
        private double crd;
        private double spd;
        private double luk;

        public double getAttackDamage() {
            return attack_damage;
        }

        public double getAttackRange() {
            return attack_range;
        }

        public double getAttackSpeed() {
            return attack_speed;
        }

        public double getMovementSpeed() {
            return movement_speed;
        }

        public double getMp() {
            return mp;
        }

        public double getMpr() {
            return mpr;
        }

        public double getAtk() {
            return atk;
        }

        public double getDef() {
            return def;
        }

        public double getMdf() {
            return mdf;
        }

        public double getCrt() {
            return crt;
        }

        public double getCrd() {
            return crd;
        }

        public double getSpd() {
            return spd;
        }

        public double getLuk() {
            return luk;
        }
    }

    public static class Buff {
        // TODO: Implement buff structure
    }

    public static class Effect {
        // TODO: Implement effect structure
    }

    // Backward compatibility methods
    public String getType() {
        return weapon_type;
    }

    public int getRequirement() {
        return required_level;
    }

    public int getPolishingCount() {
        return 0; // New structure doesn't use polishing count
    }

    public double getDamage() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackDamage() : 0;
    }

    public double getWalkSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMovementSpeed() : 0;
    }

    public double getAttackRange() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackRange() : 0;
    }

    public double getAttackSpeed() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAttackSpeed() : 0;
    }


    // 新しいステータス用のgetterメソッド
    public double getMp() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMp() : 0;
    }

    public double getMpr() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMpr() : 0;
    }

    public double getAtk() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getAtk() : 0;
    }

    public double getDef() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getDef() : 0;
    }

    public double getMdf() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getMdf() : 0;
    }

    public double getCrt() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrt() : 0;
    }

    public double getCrd() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getCrd() : 0;
    }

    public double getSpd() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getSpd() : 0;
    }

    public double getLuk() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getLuk() : 0;
    }

    // New getters
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