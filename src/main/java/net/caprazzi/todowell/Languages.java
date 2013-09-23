package net.caprazzi.todowell;

import com.google.common.base.Optional;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.FilenameUtils;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

public class Languages {

    private final BiMap<String, Language> map = HashBiMap.create();

    public Languages(Language... languages) {
        add(Arrays.asList(languages));
    }

    public final void add(Language language) {
        map.put(language.getExtension(), language);
    }

    public final void add(Collection<Language> languages) {
        for(Language language : languages) {
            add(language);
        }
    }

    public Optional<Language> fromFile(Path file) {
        String extension = FilenameUtils.getExtension(file.toString());
        return Optional.fromNullable(map.get(extension));
    }
}
