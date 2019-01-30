package model.vismo;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

@Partial
public abstract class ReferenceSupport extends ResourceObjectSupport implements Reference {

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
