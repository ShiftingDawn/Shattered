package dawn.lib;

import org.jspecify.annotations.NullUnmarked;

@NullUnmarked
public record Tuple<A, B>(A a, B b) {
}
