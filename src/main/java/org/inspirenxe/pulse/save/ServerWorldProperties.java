package org.inspirenxe.pulse.save;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.SerializationBehavior;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.storage.WorldProperties;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class ServerWorldProperties implements WorldProperties {
    private final UUID uniqueId;
    private final String worldName;
    private final DimensionType dimensionType;
    private GeneratorType generatorType;
    private GameMode gameMode;
    private SerializationBehavior serializationBehavior;
    private boolean isEnabled, loadOnStartup, doesKeepSpawnLoaded, generateSpawnOnLoad, usesMapFeatures, isHardcore, commandsAllowed, isPvpEnabled;
    private long seed;

    public ServerWorldProperties(String worldName, ServerWorldCreationSettings settings) {
        this.uniqueId = UUID.randomUUID();
        this.worldName = worldName;
        this.dimensionType = settings.getDimensionType();
        this.generatorType = settings.getGeneratorType();
        this.gameMode = settings.getGameMode();
        this.serializationBehavior = settings.getSerializationBehavior();
        this.isEnabled = settings.isEnabled();
        this.loadOnStartup = settings.loadOnStartup();
        this.doesKeepSpawnLoaded = settings.doesKeepSpawnLoaded();
        this.generateSpawnOnLoad = settings.doesGenerateSpawnOnLoad();
        this.usesMapFeatures = settings.usesMapFeatures();
        this.isHardcore = settings.isHardcore();
        this.commandsAllowed = settings.areCommandsAllowed();
        this.isPvpEnabled = settings.isPVPEnabled();
        this.seed = settings.getSeed();
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public void setEnabled(boolean state) {
        this.isEnabled = state;
    }

    @Override
    public boolean loadOnStartup() {
        return this.loadOnStartup;
    }

    @Override
    public void setLoadOnStartup(boolean state) {
        this.loadOnStartup = state;
    }

    @Override
    public boolean doesKeepSpawnLoaded() {
        return this.doesKeepSpawnLoaded;
    }

    @Override
    public void setKeepSpawnLoaded(boolean state) {
        this.doesKeepSpawnLoaded = state;
    }

    @Override
    public boolean doesGenerateSpawnOnLoad() {
        return this.generateSpawnOnLoad;
    }

    @Override
    public void setGenerateSpawnOnLoad(boolean state) {
        this.generateSpawnOnLoad = state;
    }

    @Override
    public String getWorldName() {
        return this.worldName;
    }

    @Override
    public UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override public Vector3i getSpawnPosition() {
        return null;
    }

    @Override public void setSpawnPosition(Vector3i position) {

    }

    @Override
    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    @Override
    public void setGeneratorType(GeneratorType type) {
        this.generatorType = type;
    }

    @Override
    public long getSeed() {
        return this.seed;
    }

    @Override public long getTotalTime() {
        return 0;
    }

    @Override public long getWorldTime() {
        return 0;
    }

    @Override public void setWorldTime(long time) {

    }

    @Override
    public DimensionType getDimensionType() {
        return this.dimensionType;
    }

    @Override
    public boolean isPVPEnabled() {
        return this.isPvpEnabled;
    }

    @Override
    public void setPVPEnabled(boolean enabled) {
        this.isPvpEnabled = enabled;
    }

    @Override public boolean isRaining() {
        return false;
    }

    @Override public void setRaining(boolean state) {

    }

    @Override public int getRainTime() {
        return 0;
    }

    @Override public void setRainTime(int time) {

    }

    @Override public boolean isThundering() {
        return false;
    }

    @Override public void setThundering(boolean state) {

    }

    @Override public int getThunderTime() {
        return 0;
    }

    @Override public void setThunderTime(int time) {

    }

    @Override
    public GameMode getGameMode() {
        return this.gameMode;
    }

    @Override
    public void setGameMode(GameMode gamemode) {
        this.gameMode = gamemode;
    }

    @Override
    public boolean usesMapFeatures() {
        return this.usesMapFeatures;
    }

    @Override
    public void setMapFeaturesEnabled(boolean state) {
        this.usesMapFeatures = state;
    }

    @Override
    public boolean isHardcore() {
        return this.isHardcore;
    }

    @Override
    public void setHardcore(boolean state) {
        this.isHardcore = state;
    }

    @Override
    public boolean areCommandsAllowed() {
        return this.commandsAllowed;
    }

    @Override
    public void setCommandsAllowed(boolean state) {
        this.commandsAllowed = state;
    }

    @Override public boolean isInitialized() {
        return false;
    }

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override public void setDifficulty(Difficulty difficulty) {

    }

    @Override public Vector3d getWorldBorderCenter() {
        return null;
    }

    @Override public void setWorldBorderCenter(double x, double z) {

    }

    @Override public double getWorldBorderDiameter() {
        return 0;
    }

    @Override public void setWorldBorderDiameter(double diameter) {

    }

    @Override public long getWorldBorderTimeRemaining() {
        return 0;
    }

    @Override public void setWorldBorderTimeRemaining(long time) {

    }

    @Override public double getWorldBorderTargetDiameter() {
        return 0;
    }

    @Override public void setWorldBorderTargetDiameter(double diameter) {

    }

    @Override public double getWorldBorderDamageThreshold() {
        return 0;
    }

    @Override public void setWorldBorderDamageThreshold(double distance) {

    }

    @Override public double getWorldBorderDamageAmount() {
        return 0;
    }

    @Override public void setWorldBorderDamageAmount(double damage) {

    }

    @Override public int getWorldBorderWarningTime() {
        return 0;
    }

    @Override public void setWorldBorderWarningTime(int time) {

    }

    @Override public int getWorldBorderWarningDistance() {
        return 0;
    }

    @Override public void setWorldBorderWarningDistance(int distance) {

    }

    @Override public Optional<String> getGameRule(String gameRule) {
        return null;
    }

    @Override public Map<String, String> getGameRules() {
        return null;
    }

    @Override public void setGameRule(String gameRule, String value) {

    }

    @Override public DataContainer getAdditionalProperties() {
        return null;
    }

    @Override public Optional<DataView> getPropertySection(DataQuery path) {
        return null;
    }

    @Override public void setPropertySection(DataQuery path, DataView data) {

    }

    @Override public Collection<WorldGeneratorModifier> getGeneratorModifiers() {
        return null;
    }

    @Override public void setGeneratorModifiers(Collection<WorldGeneratorModifier> modifiers) {

    }

    @Override public DataContainer getGeneratorSettings() {
        return null;
    }

    @Override
    public SerializationBehavior getSerializationBehavior() {
        return this.serializationBehavior;
    }

    @Override
    public void setSerializationBehavior(SerializationBehavior behavior) {
        this.serializationBehavior = behavior;
    }

    @Override public int getContentVersion() {
        return 0;
    }

    @Override public DataContainer toContainer() {
        return null;
    }
}
