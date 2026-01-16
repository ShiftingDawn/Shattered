package dawn.core.registry;

import dawn.Identifier;
import dawn.registry.ProtoFont;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ProtoFontImpl implements ProtoFont {

	private final @Getter Identifier registryKey;
}
