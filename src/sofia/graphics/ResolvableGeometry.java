package sofia.graphics;

import java.util.Set;

public interface ResolvableGeometry<T> extends CopyableGeometry<T>
{
    public void resolveGeometry(Shape shape);

    public boolean isGeometryResolved();

    public Set<Shape> getShapeDependencies();
}
