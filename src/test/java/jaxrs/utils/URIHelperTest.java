package jaxrs.utils;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class URIHelperTest {
    @Test
    void should_return_normalized_path_pattern() {
        String pathVariable = "1";
        final String requestPath = "/widgets/" + pathVariable;
        String resultPattern = URIHelper.normalizePath("/widgets/{id}");
        Matcher matcher = Pattern.compile(resultPattern).matcher(requestPath);
        assertThat(matcher.matches()).isTrue();
        assertThat(matcher.group(1)).isEqualTo(pathVariable);
    }
}