package model.vismo;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObjectSupport;
import model.ResourceSupport;
import org.openrdf.annotations.Precedes;

import java.util.HashSet;

@Partial
@Precedes(ResourceSupport.class)
public abstract class GroupSupport extends ResourceSupport implements Group {

    @Override
    public void addKeyword(String keyword) {

        HashSet<String> keywords = new HashSet<>();

        if (this.getKeywords() != null) {
            keywords.addAll(this.getKeywords());
        }

        keywords.add(keyword);
        this.setKeywords(keywords);
    }

    @Override
    public void addEntry(ReferenceEntry entry) {
        HashSet<ReferenceEntry> entries = new HashSet<>();

        if (this.getEntries() != null) {
            entries.addAll(this.getEntries());
        }

        entries.add(entry);
        this.setEntries(entries);
    }
}
