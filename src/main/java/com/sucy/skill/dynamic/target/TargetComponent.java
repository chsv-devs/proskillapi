package com.sucy.skill.dynamic.target;

import com.sucy.skill.SkillAPI;
import com.sucy.skill.api.target.TargetHelper;
import com.sucy.skill.cast.*;
import com.sucy.skill.dynamic.ComponentType;
import com.sucy.skill.dynamic.DynamicSkill;
import com.sucy.skill.dynamic.EffectComponent;
import com.sucy.skill.dynamic.TempEntity;
import com.sucy.skill.listener.MechanicListener;
import mc.promcteam.engine.mccore.config.parse.DataSection;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * SkillAPI © 2018
 * com.sucy.skill.dynamic.target.TargetComponent
 */
public abstract class TargetComponent extends EffectComponent {

    private static final String ALLY   = "group";
    private static final String WALL   = "wall";
    private static final String CASTER = "caster";
    private static final String MAX    = "max";

    boolean everyone;
    boolean allies;
    boolean throughWall;
    IncludeCaster self;

    @Override
    public ComponentType getType() {
        return ComponentType.TARGET;
    }

    /**
     * Executes the component
     *
     * @param caster  caster of the skill
     * @param level   level of the skill
     * @param targets targets to apply to
     *
     * @param force
     * @return true if applied to something, false otherwise
     */
    @Override
    public boolean execute(LivingEntity caster, int level, List<LivingEntity> targets, boolean force) {
        final List<LivingEntity> list = getTargets(caster, level, targets);
        return (!list.isEmpty() && executeChildren(caster, level, list, force));
    }

    @Override
    public void load(DynamicSkill skill, DataSection config) {
        super.load(skill, config);

        final String group = settings.getString(ALLY, "enemy").toLowerCase();
        everyone = group.equals("both");
        allies = group.equals("ally");
        throughWall = settings.getString(WALL, "false").equalsIgnoreCase("true");
        self = IncludeCaster.valueOf(settings.getString(CASTER, "false").toUpperCase().replace(' ', '_'));
    }

    abstract List<LivingEntity> getTargets(
            final LivingEntity caster,
            final int level,
            final List<LivingEntity> targets);

    abstract void makeIndicators(final List<IIndicator> list, final Player caster, final LivingEntity target, final int level);

    /**
     * Creates the list of indicators for the skill
     *
     * @param list   list to store indicators in
     * @param caster caster reference
     * @param targets location to base location on
     * @param level  the level of the skill to create for
     */
    @Override
    public void makeIndicators(List<IIndicator> list, Player caster, List<LivingEntity> targets, int level) {
        targets.forEach(target -> makeIndicators(list, caster, target, level));

        List<LivingEntity> childTargets = null;
        for (final EffectComponent component : children) {
            if (component.hasEffect) {
                if (childTargets == null) { childTargets = getTargets(caster, level, targets); }

                component.makeIndicators(list, caster, childTargets, level);
            }
        }
    }

    void makeConeIndicator(final List<IIndicator> list, final LivingEntity target, final double range, final double angle) {
        if (indicatorType != IndicatorType.NONE) {
            Location loc = target.getLocation();
            ConeIndicator indicator = new ConeIndicator(angle, range);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            indicator.setDirection(loc.getYaw());
            list.add(indicator);
        }
    }

    void makeCircleIndicator(final List<IIndicator> list, final LivingEntity source, final double radius) {
        if (indicatorType == IndicatorType.DIM_3) {
            final Location loc = source.getLocation();
            IIndicator indicator = new SphereIndicator(radius);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        } else if (indicatorType == IndicatorType.DIM_2) {
            final Location loc = source.getLocation();
            IIndicator indicator = new CircleIndicator(radius);
            indicator.moveTo(loc.getX(), loc.getY() + 0.1, loc.getZ());
            list.add(indicator);
        }
    }

    List<LivingEntity> determineTargets(
            final LivingEntity caster,
            final int level,
            final List<LivingEntity> from,
            final Function<LivingEntity, List<LivingEntity>> conversion) {

        final double max = parseValues(caster, MAX, level, 99);

        final List<LivingEntity> list = new ArrayList<>();
        from.forEach(target -> {
            final List<LivingEntity> found = conversion.apply(target);
            int count = 0;

            for (LivingEntity entity : found) {
                if (count >= max) break;
                if (isValidTarget(caster, target, entity) || (self.equals(IncludeCaster.IN_AREA) && caster==entity)) {
                    list.add(entity);
                    count++;
                }
            }
        });
        if (self.equals(IncludeCaster.TRUE)) list.add(caster);
        return list;
    }

    boolean isValidTarget(final LivingEntity caster, final LivingEntity from, final LivingEntity target) {
        if (SkillAPI.getMeta(target, MechanicListener.ARMOR_STAND) != null) return false;
        if (target instanceof TempEntity) return true;

        return target != caster && SkillAPI.getSettings().isValidTarget(target)
                && (throughWall || !TargetHelper.isObstructed(from.getEyeLocation(), target.getEyeLocation()))
                && (everyone || allies == SkillAPI.getSettings().isAlly(caster, target));
    }

    public enum IncludeCaster {
        TRUE, FALSE, IN_AREA
    }
}
