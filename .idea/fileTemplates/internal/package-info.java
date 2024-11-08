#parse("File Header.java")
#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")@NotNullByDefault
package ${PACKAGE_NAME};

import org.jetbrains.annotations.NotNullByDefault;
#end