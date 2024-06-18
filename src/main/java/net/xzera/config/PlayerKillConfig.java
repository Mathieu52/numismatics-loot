package net.xzera.config;

import com.moandjiezana.toml.Toml;
import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class PlayerKillConfig implements ConfigSerializable {
    private static final String PLAYER_KILL_KEY = "only-drop-when-killed-by-player";
	private static final String DEPLOYER_KILL_KEY = "allow-drop-when-killed-by-deployer";
    private static final boolean DEFAULT_PLAYER_KILL_VALUE = false;
	private static final boolean DEFAULT_DEPLOYER_KILL_VALUE = true;
    private boolean allowOnlyPlayerKillDrop;
	private boolean allowDeployerKillDrop;

    public PlayerKillConfig(boolean onlyDropWhenKilledByPlayer, boolean allowDeployerKillDrop) {
        this.setAllowOnlyPlayerKillDrop(onlyDropWhenKilledByPlayer);
		this.setAllowDeployerKillDrop(allowDeployerKillDrop);
    }

    public PlayerKillConfig() {
        this(DEFAULT_PLAYER_KILL_VALUE, DEFAULT_DEPLOYER_KILL_VALUE);
    }

    public boolean isAllowOnlyPlayerKillDrop() {
        return allowOnlyPlayerKillDrop;
    }

    public void setAllowOnlyPlayerKillDrop(boolean allowOnlyPlayerKillDrop) {
        this.allowOnlyPlayerKillDrop = allowOnlyPlayerKillDrop;
    }

	public boolean isAllowDeployerKillDrop() {
		return allowDeployerKillDrop;
	}

	public void setAllowDeployerKillDrop(boolean allowDeployerKillDrop) {
		this.allowDeployerKillDrop = allowDeployerKillDrop;
	}

	//default-drop-rate
    @Override
    public Map<String, Object> serialize(boolean includeParent) {
        Map<String, Object> data = new HashMap<>();
        data.put(PLAYER_KILL_KEY, this.isAllowOnlyPlayerKillDrop());
		data.put(DEPLOYER_KILL_KEY, this.isAllowDeployerKillDrop());

        return data;
    }

    @Override
    public void deserialize(Toml toml) throws DeserializationException {
        this.setAllowOnlyPlayerKillDrop(toml.getBoolean(PLAYER_KILL_KEY, DEFAULT_PLAYER_KILL_VALUE));
		this.setAllowDeployerKillDrop(toml.getBoolean(DEPLOYER_KILL_KEY, DEFAULT_DEPLOYER_KILL_VALUE));
    }
}
