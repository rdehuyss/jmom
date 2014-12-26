import com.google.common.collect.JMomFluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class JMomFluentIterableTest {

    @Test
    public void testForEachItem() {
        StringBuilder sb = new StringBuilder();

        List<String> strings = Lists.newArrayList("1", "2", "3", "4");
        JMomFluentIterable.from(strings)
                .forEachItem(item -> sb.append(item));

        assertThat(sb.toString()).isEqualTo("1234");
    }

}