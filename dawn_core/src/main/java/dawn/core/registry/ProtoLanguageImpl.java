package dawn.core.registry;

import java.util.Map;
import dawn.Identifier;
import dawn.registry.ProtoLanguage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public final class ProtoLanguageImpl implements ProtoLanguage {

	private final Identifier registryKey;
	private final Map<String, String> entries;
}
