package net.alcaris.plugin.core.model.item;

@SuppressWarnings("unused")
public class Common {
    public static class Effect {
        private String effect;
        private int duration;
        private int amplifier;
        private double chance;

        public String getEffect() {
            return effect;
        }

        public int getDuration() {
            return duration;
        }

        public int getAmplifier() {
            return amplifier;
        }

        public double getChance() {
            return chance;
        }
    }

    public static class Attribute {
        private String attribute;
        private String operation;
        private int value;
        private int duration;

        public String getAttribute() {
            return attribute;
        }

        public String getOperation() {
            return operation;
        }

        public int getValue() {
            return value;
        }

        public int getDuration() {
            return duration;
        }
    }

    public static class Buff {
        private String kind;
        private int duration;
        private float amount;

        public String getKind() {
            return kind;
        }

        public int getDuration() {
            return duration;
        }

        public float getAmount() {
            return amount;
        }
    }
}
