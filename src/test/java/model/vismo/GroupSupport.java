package model.vismo;

import com.github.anno4j.annotations.Partial;
import com.github.anno4j.model.Target;
import com.github.anno4j.model.impl.ResourceObjectSupport;

import java.util.HashSet;

@Partial
public abstract class GroupSupport extends ResourceObjectSupport implements Group {

    @Override
    public void addKeyword(String keyword) {

        HashSet<String> keywords = new HashSet<>();

        if (this.getKeywords() != null) {
            keywords.addAll(this.getKeywords());
        }

        keywords.add(keyword);
        this.setKeywords(keywords);
    }
}
