package net.xzera.config;

import com.moandjiezana.toml.Toml;

import net.xzera.config.Exception.DeserializationException;

import java.util.HashMap;
import java.util.Map;

public class GrantBookConfig implements ConfigSerializable {
	private static final String KEY = "grant-book-on-first-join";
	private static final boolean DEFAULT_GRANT_BOOK_VALUE = false;
	private boolean grantBook;

	public GrantBookConfig(boolean allowed) {
		this.setGrantBook(allowed);
	}

	public GrantBookConfig() {
		this(DEFAULT_GRANT_BOOK_VALUE);
	}

	public boolean isGrantBook() {
		return grantBook;
	}

	public void setGrantBook(boolean grantBook) {
		this.grantBook = grantBook;
	}

	//default-drop-rate
	@Override
	public Map<String, Object> serialize(boolean includeParent) {
		Map<String, Object> data = new HashMap<>();
		data.put(KEY, this.isGrantBook());

		return data;
	}

	@Override
	public void deserialize(Toml toml) throws DeserializationException {
		this.setGrantBook(toml.getBoolean(KEY, DEFAULT_GRANT_BOOK_VALUE));
	}
}
