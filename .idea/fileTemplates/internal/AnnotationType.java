#if (${PACKAGE_NAME} && ${PACKAGE_NAME} != "")package ${PACKAGE_NAME};

#end
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
#parse("File Header.java")

@Retention(RetentionPolicy.RUNTIME)
public @interface ${NAME} {
}
