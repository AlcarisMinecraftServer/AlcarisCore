package net.alcaris.plugin.core.model.item;

import java.util.List;

@SuppressWarnings("unused")
public class ItemFoodModel {
    private int nutrition;
    private float saturation;
    private boolean can_always_eat;
    private float eat_seconds;
    private List<Common.Effect> effects;
    private List<Common.Attribute> attributes;
    private List<Common.Buff> buffs;

    public int getNutrition() {
        return nutrition;
    }

    public float getSaturation() {
        return saturation;
    }

    public boolean isCanAlwaysEat() {
        return can_always_eat;
    }

    public float getEatSeconds() {
        return eat_seconds;
    }

    public List<Common.Effect> getEffects() {
        return effects;
    }

    public List<Common.Attribute> getAttributes() {
        return attributes;
    }

    public List<Common.Buff> getBuff() {
        return buffs;
    }
}
