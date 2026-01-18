package dawn.core.dawndb;

import java.io.IOException;

@FunctionalInterface
interface IOFunction<A, B> {

	void apply(A a, B b) throws IOException;
}
