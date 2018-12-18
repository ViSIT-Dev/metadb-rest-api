package model.vismo;


import model.Resource;
import model.VISMO;
import model.cidoc.E74Group;
import org.openrdf.annotations.Iri;

@Iri(VISMO.GROUP)
public interface Group extends E74Group, Resource {
}
