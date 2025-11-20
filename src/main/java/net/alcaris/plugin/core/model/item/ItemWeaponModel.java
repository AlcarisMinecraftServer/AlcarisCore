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
        private double attack_damage;
        private double attack_range;
        private double attack_speed;
        private double drop_rate_bonus;
        private double experience_bonus;
        private double movement_speed;

        public double getAttackDamage() {
            return attack_damage;
        }

        public double getAttackRange() {
            return attack_range;
        }

        public double getAttackSpeed() {
            return attack_speed;
        }

        public double getDropRateBonus() {
            return drop_rate_bonus;
        }

        public double getExperienceBonus() {
            return experience_bonus;
        }

        public double getMovementSpeed() {
            return movement_speed;
        }
    }

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

    public double getXpBonus() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getExperienceBonus() : 0;
    }

    public double getLootBonus() {
        return base != null && base.getAttributes() != null ? base.getAttributes().getDropRateBonus() : 0;
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