package seTryout;

import org.neo4j.graphdb.*;

import java.util.Map;

public abstract class Unit implements Node {

    private Node node;

    protected Unit(ComputeBehaviour computeBehaviour, Node node) {
        this.computeBehaviour = computeBehaviour;
        computeBehaviour.setNode(node);
        this.node = node;
    }

    // delegation of Node methods to wrapped node
    @Override
    public final long getId() {
        return node.getId();
    }

    @Override
    public final void delete() {
        node.delete();
    }

    @Override
    public final Iterable<Relationship> getRelationships() {
        return node.getRelationships();
    }

    @Override
    public final boolean hasRelationship() {
        return node.hasRelationship();
    }

    @Override
    public final Iterable<Relationship> getRelationships(RelationshipType... relationshipTypes) {
        return node.getRelationships(relationshipTypes);
    }

    @Override
    public final Iterable<Relationship> getRelationships(Direction direction, RelationshipType... relationshipTypes) {
        return node.getRelationships(direction, relationshipTypes);
    }

    @Override
    public final boolean hasRelationship(RelationshipType... relationshipTypes) {
        return node.hasRelationship(relationshipTypes);
    }

    @Override
    public final boolean hasRelationship(Direction direction, RelationshipType... relationshipTypes) {
        return node.hasRelationship(direction, relationshipTypes);
    }

    @Override
    public final Iterable<Relationship> getRelationships(Direction direction) {
        return node.getRelationships(direction);
    }

    @Override
    public final boolean hasRelationship(Direction direction) {
        return node.hasRelationship(direction);
    }

    @Override
    public final Iterable<Relationship> getRelationships(RelationshipType relationshipType, Direction direction) {
        return node.getRelationships(relationshipType, direction);
    }

    @Override
    public final boolean hasRelationship(RelationshipType relationshipType, Direction direction) {
        return node.hasRelationship(relationshipType, direction);
    }

    @Override
    public final Relationship getSingleRelationship(RelationshipType relationshipType, Direction direction) {
        return node.getSingleRelationship(relationshipType, direction);
    }

    @Override
    public final Relationship createRelationshipTo(Node node, RelationshipType relationshipType) {
        return this.node.createRelationshipTo(node, relationshipType);
    }

    @Override
    public final Iterable<RelationshipType> getRelationshipTypes() {
        return node.getRelationshipTypes();
    }

    @Override
    public final int getDegree() {
        return node.getDegree();
    }

    @Override
    public final int getDegree(RelationshipType relationshipType) {
        return node.getDegree(relationshipType);
    }

    @Override
    public final int getDegree(Direction direction) {
        return node.getDegree(direction);
    }

    @Override
    public final int getDegree(RelationshipType relationshipType, Direction direction) {
        return node.getDegree(relationshipType, direction);
    }

    @Override
    public final void addLabel(Label label) {
        node.addLabel(label);
    }

    @Override
    public final void removeLabel(Label label) {
        node.removeLabel(label);
    }

    @Override
    public final boolean hasLabel(Label label) {
        return node.hasLabel(label);
    }

    @Override
    public final Iterable<Label> getLabels() {
        return node.getLabels();
    }

    @Override
    public final GraphDatabaseService getGraphDatabase() {
        return node.getGraphDatabase();
    }

    @Override
    public final boolean hasProperty(String s) {
        return node.hasProperty(s);
    }

    @Override
    public final Object getProperty(String s) {
        return node.getProperty(s);
    }

    @Override
    public final Object getProperty(String s, Object o) {
        return node.getProperty(s, o);
    }

    @Override
    public final void setProperty(String s, Object o) {
        node.setProperty(s, o);
    }

    @Override
    public final Object removeProperty(String s) {
        return node.removeProperty(s);
    }

    @Override
    public final Iterable<String> getPropertyKeys() {
        return node.getPropertyKeys();
    }

    @Override
    public final Map<String, Object> getProperties(String... strings) {
        return node.getProperties(strings);
    }

    @Override
    public final Map<String, Object> getAllProperties() {
        return node.getAllProperties();
    }

    ComputeBehaviour computeBehaviour;

    public double compute() {
        return computeBehaviour.compute();
    }
}
