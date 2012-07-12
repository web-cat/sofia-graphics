package sofia.graphics;

public class SizeF
{
    public float width;
    public float height;


    // ----------------------------------------------------------
    public SizeF(float width, float height)
    {
        this.width = width;
        this.height = height;
    }


    // ----------------------------------------------------------
    @Override
    public String toString()
    {
        return "(" + width + ", " + height + ")";
    }
}
