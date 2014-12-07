/*
 * Copyright (C) 2011 Virginia Tech Department of Computer Science
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sofia.graphics;

import sofia.app.Persistor;
import sofia.graphics.internal.ColorPersistor;
import sofia.util.Random;

//-------------------------------------------------------------------------
/**
 * <p>
 * Represents a color, which is composed of red, green, blue, and alpha
 * components, which are integers between 0 and 255. Color objects are
 * immutable &ndash; once one is created, it is not not possible to change its
 * color components. Methods like {@link #brighter()} and {@link #darker()}
 * return a new color rather than modify the receiver.
 * </p><p>
 * Colors cannot be created using a constructor. Instead, there are a few
 * <em>static factory methods</em> that you can call to obtain instances of
 * this class. These methods let you get a color by:
 * </p>
 * <ul>
 * <li>Passing an Android color integer ({@link #fromRawColor(int)}, see
 * below)</li>
 * <li>Asking for a random color ({@link #getRandomColor()})</li>
 * <li>Passing a gray level ({@link #gray(int)}, {@link #gray(int, int)})</li>
 * <li>Passing hue/saturation/value components
 * ({@link #hsv(float, float, float)},
 * {@link #hsv(float, float, float, int)})</li>
 * <li>Passing red/green/blue components ({@link #rgb(int, int, int)},
 * {@link #rgb(int, int, int, int)})</li>
 * </ul>
 * <p>
 * In traditional Android programming, colors are merely represented as plain
 * integers, and the {@link android.graphics.Color} class provides static
 * helper methods for manipulating those colors. This {@code Color} class
 * treats colors as actual objects, with useful methods on the objects
 * themselves to manipulate them. If you need to pass a {@code Color} object to
 * a traditional Android method that expects a color integer, use the
 * {@link #toRawColor()} method. Likewise, to create a {@code Color} object
 * from an integer retrieved from a traditional Android API, use the
 * {@link #fromRawColor(int)} static factory method.
 * </p>
 *
 * @author Tony Allevato
 */
@Persistor(ColorPersistor.class)
public class Color
{
    //~ Constants .............................................................

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F0F8FF"></div> The named color "Alice blue" (HTML color code #F0F8FF). */
    public static final Color aliceBlue = rgb(0xF0, 0xF8, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FAEBD7"></div> The named color "Antique white" (HTML color code #FAEBD7). */
    public static final Color antiqueWhite = rgb(0xFA, 0xEB, 0xD7);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00FFFF"></div> The named color "Aqua" (HTML color code #00FFFF). */
    public static final Color aqua = rgb(0x00, 0xFF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #7FFFD4"></div> The named color "Aquamarine" (HTML color code #7FFFD4). */
    public static final Color aquamarine = rgb(0x7F, 0xFF, 0xD4);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F0FFFF"></div> The named color "Azure" (HTML color code #F0FFFF). */
    public static final Color azure = rgb(0xF0, 0xFF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F5F5DC"></div> The named color "Beige" (HTML color code #F5F5DC). */
    public static final Color beige = rgb(0xF5, 0xF5, 0xDC);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFE4C4"></div> The named color "Bisque" (HTML color code #FFE4C4). */
    public static final Color bisque = rgb(0xFF, 0xE4, 0xC4);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #000000"></div> The named color "Black" (HTML color code #000000). */
    public static final Color black = rgb(0x00, 0x00, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFEBCD"></div> The named color "Blanched almond" (HTML color code #FFEBCD). */
    public static final Color blanchedAlmond = rgb(0xFF, 0xEB, 0xCD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #0000FF"></div> The named color "Blue" (HTML color code #0000FF). */
    public static final Color blue = rgb(0x00, 0x00, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #8A2BE2"></div> The named color "Blue violet" (HTML color code #8A2BE2). */
    public static final Color blueViolet = rgb(0x8A, 0x2B, 0xE2);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #A52A2A"></div> The named color "Brown" (HTML color code #A52A2A). */
    public static final Color brown = rgb(0xA5, 0x2A, 0x2A);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DEB887"></div> The named color "Burly wood" (HTML color code #DEB887). */
    public static final Color burlyWood = rgb(0xDE, 0xB8, 0x87);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #5F9EA0"></div> The named color "Cadet blue" (HTML color code #5F9EA0). */
    public static final Color cadetBlue = rgb(0x5F, 0x9E, 0xA0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #7FFF00"></div> The named color "Chartreuse" (HTML color code #7FFF00). */
    public static final Color chartreuse = rgb(0x7F, 0xFF, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #D2691E"></div> The named color "Chocolate" (HTML color code #D2691E). */
    public static final Color chocolate = rgb(0xD2, 0x69, 0x1E);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF7F50"></div> The named color "Coral" (HTML color code #FF7F50). */
    public static final Color coral = rgb(0xFF, 0x7F, 0x50);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #6495ED"></div> The named color "Cornflower blue" (HTML color code #6495ED). */
    public static final Color cornflowerBlue = rgb(0x64, 0x95, 0xED);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFF8DC"></div> The named color "Cornsilk" (HTML color code #FFF8DC). */
    public static final Color cornsilk = rgb(0xFF, 0xF8, 0xDC);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DC143C"></div> The named color "Crimson" (HTML color code #DC143C). */
    public static final Color crimson = rgb(0xDC, 0x14, 0x3C);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00FFFF"></div> The named color "Cyan" (HTML color code #00FFFF). */
    public static final Color cyan = rgb(0x00, 0xFF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00008B"></div> The named color "Dark blue" (HTML color code #00008B). */
    public static final Color darkBlue = rgb(0x00, 0x00, 0x8B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #008B8B"></div> The named color "Dark cyan" (HTML color code #008B8B). */
    public static final Color darkCyan = rgb(0x00, 0x8B, 0x8B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #B8860B"></div> The named color "Dark golden rod" (HTML color code #B8860B). */
    public static final Color darkGoldenRod = rgb(0xB8, 0x86, 0x0B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #A9A9A9"></div> The named color "Dark gray" (HTML color code #A9A9A9). */
    public static final Color darkGray = rgb(0xA9, 0xA9, 0xA9);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #006400"></div> The named color "Dark green" (HTML color code #006400). */
    public static final Color darkGreen = rgb(0x00, 0x64, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #BDB76B"></div> The named color "Dark khaki" (HTML color code #BDB76B). */
    public static final Color darkKhaki = rgb(0xBD, 0xB7, 0x6B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #8B008B"></div> The named color "Dark magenta" (HTML color code #8B008B). */
    public static final Color darkMagenta = rgb(0x8B, 0x00, 0x8B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #556B2F"></div> The named color "Dark olive green" (HTML color code #556B2F). */
    public static final Color darkOliveGreen = rgb(0x55, 0x6B, 0x2F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF8C00"></div> The named color "Dark orange" (HTML color code #FF8C00). */
    public static final Color darkOrange = rgb(0xFF, 0x8C, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #9932CC"></div> The named color "Dark orchid" (HTML color code #9932CC). */
    public static final Color darkOrchid = rgb(0x99, 0x32, 0xCC);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #8B0000"></div> The named color "Dark red" (HTML color code #8B0000). */
    public static final Color darkRed = rgb(0x8B, 0x00, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #E9967A"></div> The named color "Dark salmon" (HTML color code #E9967A). */
    public static final Color darkSalmon = rgb(0xE9, 0x96, 0x7A);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #8FBC8F"></div> The named color "Dark sea green" (HTML color code #8FBC8F). */
    public static final Color darkSeaGreen = rgb(0x8F, 0xBC, 0x8F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #483D8B"></div> The named color "Dark slate blue" (HTML color code #483D8B). */
    public static final Color darkSlateBlue = rgb(0x48, 0x3D, 0x8B);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #2F4F4F"></div> The named color "Dark slate gray" (HTML color code #2F4F4F). */
    public static final Color darkSlateGray = rgb(0x2F, 0x4F, 0x4F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00CED1"></div> The named color "Dark turquoise" (HTML color code #00CED1). */
    public static final Color darkTurquoise = rgb(0x00, 0xCE, 0xD1);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #9400D3"></div> The named color "Dark violet" (HTML color code #9400D3). */
    public static final Color darkViolet = rgb(0x94, 0x00, 0xD3);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF1493"></div> The named color "Deep pink" (HTML color code #FF1493). */
    public static final Color deepPink = rgb(0xFF, 0x14, 0x93);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00BFFF"></div> The named color "Deep sky blue" (HTML color code #00BFFF). */
    public static final Color deepSkyBlue = rgb(0x00, 0xBF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #696969"></div> The named color "Dim gray" (HTML color code #696969). */
    public static final Color dimGray = rgb(0x69, 0x69, 0x69);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #1E90FF"></div> The named color "Dodger blue" (HTML color code #1E90FF). */
    public static final Color dodgerBlue = rgb(0x1E, 0x90, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #B22222"></div> The named color "Fire brick" (HTML color code #B22222). */
    public static final Color fireBrick = rgb(0xB2, 0x22, 0x22);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFAF0"></div> The named color "Floral white" (HTML color code #FFFAF0). */
    public static final Color floralWhite = rgb(0xFF, 0xFA, 0xF0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #228B22"></div> The named color "Forest green" (HTML color code #228B22). */
    public static final Color forestGreen = rgb(0x22, 0x8B, 0x22);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF00FF"></div> The named color "Fuchsia" (HTML color code #FF00FF). */
    public static final Color fuchsia = rgb(0xFF, 0x00, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DCDCDC"></div> The named color "Gainsboro" (HTML color code #DCDCDC). */
    public static final Color gainsboro = rgb(0xDC, 0xDC, 0xDC);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F8F8FF"></div> The named color "Ghost white" (HTML color code #F8F8FF). */
    public static final Color ghostWhite = rgb(0xF8, 0xF8, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFD700"></div> The named color "Gold" (HTML color code #FFD700). */
    public static final Color gold = rgb(0xFF, 0xD7, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DAA520"></div> The named color "Golden rod" (HTML color code #DAA520). */
    public static final Color goldenRod = rgb(0xDA, 0xA5, 0x20);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #808080"></div> The named color "Gray" (HTML color code #808080). */
    public static final Color gray = rgb(0x80, 0x80, 0x80);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #008000"></div> The named color "Green" (HTML color code #008000). */
    public static final Color green = rgb(0x00, 0x80, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #ADFF2F"></div> The named color "Green yellow" (HTML color code #ADFF2F). */
    public static final Color greenYellow = rgb(0xAD, 0xFF, 0x2F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F0FFF0"></div> The named color "Honey dew" (HTML color code #F0FFF0). */
    public static final Color honeyDew = rgb(0xF0, 0xFF, 0xF0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF69B4"></div> The named color "Hot pink" (HTML color code #FF69B4). */
    public static final Color hotPink = rgb(0xFF, 0x69, 0xB4);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #CD5C5C"></div> The named color "Indian red" (HTML color code #CD5C5C). */
    public static final Color indianRed = rgb(0xCD, 0x5C, 0x5C);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #4B0082"></div> The named color "Indigo" (HTML color code #4B0082). */
    public static final Color indigo = rgb(0x4B, 0x00, 0x82);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFFF0"></div> The named color "Ivory" (HTML color code #FFFFF0). */
    public static final Color ivory = rgb(0xFF, 0xFF, 0xF0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F0E68C"></div> The named color "Khaki" (HTML color code #F0E68C). */
    public static final Color khaki = rgb(0xF0, 0xE6, 0x8C);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #E6E6FA"></div> The named color "Lavender" (HTML color code #E6E6FA). */
    public static final Color lavender = rgb(0xE6, 0xE6, 0xFA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFF0F5"></div> The named color "Lavender blush" (HTML color code #FFF0F5). */
    public static final Color lavenderBlush = rgb(0xFF, 0xF0, 0xF5);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #7CFC00"></div> The named color "Lawn green" (HTML color code #7CFC00). */
    public static final Color lawnGreen = rgb(0x7C, 0xFC, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFACD"></div> The named color "Lemon chiffon" (HTML color code #FFFACD). */
    public static final Color lemonChiffon = rgb(0xFF, 0xFA, 0xCD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #ADD8E6"></div> The named color "Light blue" (HTML color code #ADD8E6). */
    public static final Color lightBlue = rgb(0xAD, 0xD8, 0xE6);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F08080"></div> The named color "Light coral" (HTML color code #F08080). */
    public static final Color lightCoral = rgb(0xF0, 0x80, 0x80);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #E0FFFF"></div> The named color "Light cyan" (HTML color code #E0FFFF). */
    public static final Color lightCyan = rgb(0xE0, 0xFF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FAFAD2"></div> The named color "Light golden rod yellow" (HTML color code #FAFAD2). */
    public static final Color lightGoldenRodYellow = rgb(0xFA, 0xFA, 0xD2);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #D3D3D3"></div> The named color "Light gray" (HTML color code #D3D3D3). */
    public static final Color lightGray = rgb(0xD3, 0xD3, 0xD3);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #90EE90"></div> The named color "Light green" (HTML color code #90EE90). */
    public static final Color lightGreen = rgb(0x90, 0xEE, 0x90);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFB6C1"></div> The named color "Light pink" (HTML color code #FFB6C1). */
    public static final Color lightPink = rgb(0xFF, 0xB6, 0xC1);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFA07A"></div> The named color "Light salmon" (HTML color code #FFA07A). */
    public static final Color lightSalmon = rgb(0xFF, 0xA0, 0x7A);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #20B2AA"></div> The named color "Light sea green" (HTML color code #20B2AA). */
    public static final Color lightSeaGreen = rgb(0x20, 0xB2, 0xAA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #87CEFA"></div> The named color "Light sky blue" (HTML color code #87CEFA). */
    public static final Color lightSkyBlue = rgb(0x87, 0xCE, 0xFA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #778899"></div> The named color "Light slate gray" (HTML color code #778899). */
    public static final Color lightSlateGray = rgb(0x77, 0x88, 0x99);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #B0C4DE"></div> The named color "Light steel blue" (HTML color code #B0C4DE). */
    public static final Color lightSteelBlue = rgb(0xB0, 0xC4, 0xDE);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFFE0"></div> The named color "Light yellow" (HTML color code #FFFFE0). */
    public static final Color lightYellow = rgb(0xFF, 0xFF, 0xE0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00FF00"></div> The named color "Lime" (HTML color code #00FF00). */
    public static final Color lime = rgb(0x00, 0xFF, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #32CD32"></div> The named color "Lime green" (HTML color code #32CD32). */
    public static final Color limeGreen = rgb(0x32, 0xCD, 0x32);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FAF0E6"></div> The named color "Linen" (HTML color code #FAF0E6). */
    public static final Color linen = rgb(0xFA, 0xF0, 0xE6);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF00FF"></div> The named color "Magenta" (HTML color code #FF00FF). */
    public static final Color magenta = rgb(0xFF, 0x00, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #800000"></div> The named color "Maroon" (HTML color code #800000). */
    public static final Color maroon = rgb(0x80, 0x00, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #66CDAA"></div> The named color "Medium aquamarine" (HTML color code #66CDAA). */
    public static final Color mediumAquamarine = rgb(0x66, 0xCD, 0xAA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #0000CD"></div> The named color "Medium blue" (HTML color code #0000CD). */
    public static final Color mediumBlue = rgb(0x00, 0x00, 0xCD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #BA55D3"></div> The named color "Medium orchid" (HTML color code #BA55D3). */
    public static final Color mediumOrchid = rgb(0xBA, 0x55, 0xD3);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #9370D8"></div> The named color "Medium purple" (HTML color code #9370D8). */
    public static final Color mediumPurple = rgb(0x93, 0x70, 0xD8);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #3CB371"></div> The named color "Medium sea green" (HTML color code #3CB371). */
    public static final Color mediumSeaGreen = rgb(0x3C, 0xB3, 0x71);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #7B68EE"></div> The named color "Medium slate blue" (HTML color code #7B68EE). */
    public static final Color mediumSlateBlue = rgb(0x7B, 0x68, 0xEE);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00FA9A"></div> The named color "Medium spring green" (HTML color code #00FA9A). */
    public static final Color mediumSpringGreen = rgb(0x00, 0xFA, 0x9A);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #48D1CC"></div> The named color "Medium turquoise" (HTML color code #48D1CC). */
    public static final Color mediumTurquoise = rgb(0x48, 0xD1, 0xCC);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #C71585"></div> The named color "Medium violet red" (HTML color code #C71585). */
    public static final Color mediumVioletRed = rgb(0xC7, 0x15, 0x85);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #191970"></div> The named color "Midnight blue" (HTML color code #191970). */
    public static final Color midnightBlue = rgb(0x19, 0x19, 0x70);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F5FFFA"></div> The named color "Mint cream" (HTML color code #F5FFFA). */
    public static final Color mintCream = rgb(0xF5, 0xFF, 0xFA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFE4E1"></div> The named color "Misty rose" (HTML color code #FFE4E1). */
    public static final Color mistyRose = rgb(0xFF, 0xE4, 0xE1);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFE4B5"></div> The named color "Moccasin" (HTML color code #FFE4B5). */
    public static final Color moccasin = rgb(0xFF, 0xE4, 0xB5);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFDEAD"></div> The named color "Navajo white" (HTML color code #FFDEAD). */
    public static final Color navajoWhite = rgb(0xFF, 0xDE, 0xAD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #000080"></div> The named color "Navy" (HTML color code #000080). */
    public static final Color navy = rgb(0x00, 0x00, 0x80);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FDF5E6"></div> The named color "Old lace" (HTML color code #FDF5E6). */
    public static final Color oldLace = rgb(0xFD, 0xF5, 0xE6);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #808000"></div> The named color "Olive" (HTML color code #808000). */
    public static final Color olive = rgb(0x80, 0x80, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #6B8E23"></div> The named color "Olive drab" (HTML color code #6B8E23). */
    public static final Color oliveDrab = rgb(0x6B, 0x8E, 0x23);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFA500"></div> The named color "Orange" (HTML color code #FFA500). */
    public static final Color orange = rgb(0xFF, 0xA5, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF4500"></div> The named color "Orange red" (HTML color code #FF4500). */
    public static final Color orangeRed = rgb(0xFF, 0x45, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DA70D6"></div> The named color "Orchid" (HTML color code #DA70D6). */
    public static final Color orchid = rgb(0xDA, 0x70, 0xD6);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #EEE8AA"></div> The named color "Pale golden rod" (HTML color code #EEE8AA). */
    public static final Color paleGoldenRod = rgb(0xEE, 0xE8, 0xAA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #98FB98"></div> The named color "Pale green" (HTML color code #98FB98). */
    public static final Color paleGreen = rgb(0x98, 0xFB, 0x98);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #AFEEEE"></div> The named color "Pale turquoise" (HTML color code #AFEEEE). */
    public static final Color paleTurquoise = rgb(0xAF, 0xEE, 0xEE);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #D87093"></div> The named color "Pale violet red" (HTML color code #D87093). */
    public static final Color paleVioletRed = rgb(0xD8, 0x70, 0x93);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFEFD5"></div> The named color "Papaya whip" (HTML color code #FFEFD5). */
    public static final Color papayaWhip = rgb(0xFF, 0xEF, 0xD5);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFDAB9"></div> The named color "Peach puff" (HTML color code #FFDAB9). */
    public static final Color peachPuff = rgb(0xFF, 0xDA, 0xB9);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #CD853F"></div> The named color "Peru" (HTML color code #CD853F). */
    public static final Color peru = rgb(0xCD, 0x85, 0x3F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFC0CB"></div> The named color "Pink" (HTML color code #FFC0CB). */
    public static final Color pink = rgb(0xFF, 0xC0, 0xCB);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #DDA0DD"></div> The named color "Plum" (HTML color code #DDA0DD). */
    public static final Color plum = rgb(0xDD, 0xA0, 0xDD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #B0E0E6"></div> The named color "Powder blue" (HTML color code #B0E0E6). */
    public static final Color powderBlue = rgb(0xB0, 0xE0, 0xE6);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #800080"></div> The named color "Purple" (HTML color code #800080). */
    public static final Color purple = rgb(0x80, 0x00, 0x80);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF0000"></div> The named color "Red" (HTML color code #FF0000). */
    public static final Color red = rgb(0xFF, 0x00, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #BC8F8F"></div> The named color "Rosy brown" (HTML color code #BC8F8F). */
    public static final Color rosyBrown = rgb(0xBC, 0x8F, 0x8F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #4169E1"></div> The named color "Royal blue" (HTML color code #4169E1). */
    public static final Color royalBlue = rgb(0x41, 0x69, 0xE1);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #8B4513"></div> The named color "Saddle brown" (HTML color code #8B4513). */
    public static final Color saddleBrown = rgb(0x8B, 0x45, 0x13);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FA8072"></div> The named color "Salmon" (HTML color code #FA8072). */
    public static final Color salmon = rgb(0xFA, 0x80, 0x72);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F4A460"></div> The named color "Sandy brown" (HTML color code #F4A460). */
    public static final Color sandyBrown = rgb(0xF4, 0xA4, 0x60);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #2E8B57"></div> The named color "Sea green" (HTML color code #2E8B57). */
    public static final Color seaGreen = rgb(0x2E, 0x8B, 0x57);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFF5EE"></div> The named color "Sea shell" (HTML color code #FFF5EE). */
    public static final Color seaShell = rgb(0xFF, 0xF5, 0xEE);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #A0522D"></div> The named color "Sienna" (HTML color code #A0522D). */
    public static final Color sienna = rgb(0xA0, 0x52, 0x2D);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #C0C0C0"></div> The named color "Silver" (HTML color code #C0C0C0). */
    public static final Color silver = rgb(0xC0, 0xC0, 0xC0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #87CEEB"></div> The named color "Sky blue" (HTML color code #87CEEB). */
    public static final Color skyBlue = rgb(0x87, 0xCE, 0xEB);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #6A5ACD"></div> The named color "Slate blue" (HTML color code #6A5ACD). */
    public static final Color slateBlue = rgb(0x6A, 0x5A, 0xCD);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #708090"></div> The named color "Slate gray" (HTML color code #708090). */
    public static final Color slateGray = rgb(0x70, 0x80, 0x90);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFAFA"></div> The named color "Snow" (HTML color code #FFFAFA). */
    public static final Color snow = rgb(0xFF, 0xFA, 0xFA);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #00FF7F"></div> The named color "Spring green" (HTML color code #00FF7F). */
    public static final Color springGreen = rgb(0x00, 0xFF, 0x7F);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #4682B4"></div> The named color "Steel blue" (HTML color code #4682B4). */
    public static final Color steelBlue = rgb(0x46, 0x82, 0xB4);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #D2B48C"></div> The named color "Tan" (HTML color code #D2B48C). */
    public static final Color tan = rgb(0xD2, 0xB4, 0x8C);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #008080"></div> The named color "Teal" (HTML color code #008080). */
    public static final Color teal = rgb(0x00, 0x80, 0x80);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #D8BFD8"></div> The named color "Thistle" (HTML color code #D8BFD8). */
    public static final Color thistle = rgb(0xD8, 0xBF, 0xD8);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FF6347"></div> The named color "Tomato" (HTML color code #FF6347). */
    public static final Color tomato = rgb(0xFF, 0x63, 0x47);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #40E0D0"></div> The named color "Turquoise" (HTML color code #40E0D0). */
    public static final Color turquoise = rgb(0x40, 0xE0, 0xD0);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #EE82EE"></div> The named color "Violet" (HTML color code #EE82EE). */
    public static final Color violet = rgb(0xEE, 0x82, 0xEE);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F5DEB3"></div> The named color "Wheat" (HTML color code #F5DEB3). */
    public static final Color wheat = rgb(0xF5, 0xDE, 0xB3);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFFFF"></div> The named color "White" (HTML color code #FFFFFF). */
    public static final Color white = rgb(0xFF, 0xFF, 0xFF);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #F5F5F5"></div> The named color "White smoke" (HTML color code #F5F5F5). */
    public static final Color whiteSmoke = rgb(0xF5, 0xF5, 0xF5);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #FFFF00"></div> The named color "Yellow" (HTML color code #FFFF00). */
    public static final Color yellow = rgb(0xFF, 0xFF, 0x00);

    /** <div style="display: inline-block; width: 2em; height: 1em; border: 1px solid gray; vertical-align: bottom; background-color: #9ACD32"></div> The named color "Yellow green" (HTML color code #9ACD32). */
    public static final Color yellowGreen = rgb(0x9A, 0xCD, 0x32);

    /** A convenience value representing a clear/transparent color. */
    public static final Color clear = rgb(0, 0, 0, 0);


    //~ Fields ................................................................

    private int rawColor;
    private float[] hsv;

    private static final double FACTOR = 0.7;


    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Creates a new {@code Color} with the specified integer value. Only used
     * internally; users should create colors using the various static factory
     * methods.
     *
     * @param rawColor the raw color integer
     */
    private Color(int rawColor)
    {
        this.rawColor = rawColor;
    }


    //~ Methods ...............................................................

    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that represents a gray level, where a gray level of
     * 0 is black and a gray level of 255 is white. Pure gray levels have red,
     * green, and blue components that are all equal.
     *
     * @param gray the gray level, between 0 and 255
     * @return a {@code Color} that represents the gray level
     */
    public static Color gray(int gray)
    {
        return fromRawColor(android.graphics.Color.rgb(gray, gray, gray));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that represents a gray level with the specified
     * alpha, where a gray level of 0 is black and a gray level of 255 is
     * white. Pure gray levels have red, green, and blue components that are
     * all equal.
     *
     * @param gray the gray level, between 0 and 255
     * @param alpha the alpha level, between 0 and 255
     * @return a {@code Color} that represents the gray level
     */
    public static Color gray(int gray, int alpha)
    {
        return fromRawColor(android.graphics.Color.argb(
                alpha, gray, gray, gray));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that is composed of the specified red, green, and
     * blue components.
     *
     * @param red the red component, between 0 and 255
     * @param green the green component, between 0 and 255
     * @param blue the blue component, between 0 and 255
     * @return a {@code Color} composed of the specified red, green, and blue
     *     components
     */
    public static Color rgb(int red, int green, int blue)
    {
        return fromRawColor(android.graphics.Color.rgb(red, green, blue));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that is composed of the specified red, green, blue,
     * and alpha components.
     *
     * @param red the red component, between 0 and 255
     * @param green the green component, between 0 and 255
     * @param blue the blue component, between 0 and 255
     * @param alpha the alpha component, between 0 and 255
     * @return a {@code Color} composed of the specified red, green, blue, and
     *     alpha components
     */
    public static Color rgb(int red, int green, int blue, int alpha)
    {
        return fromRawColor(android.graphics.Color.argb(
                alpha, red, green, blue));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that is composed of the specified hue, saturation,
     * and value components.
     *
     * @param hue the hue component, between 0 (inclusive) and 360 (exclusive)
     * @param saturation the saturation component, between 0 and 1
     * @param value the value component, between 0 and 1
     * @return a {@code Color} composed of the specified hue, saturation, and
     *     value components
     */
    public static Color hsv(float hue, float saturation, float value)
    {
        return fromRawColor(android.graphics.Color.HSVToColor(
                new float[] { hue, saturation, value }));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that is composed of the specified hue, saturation,
     * value, and alpha components.
     *
     * @param hue the hue component, between 0 (inclusive) and 360 (exclusive)
     * @param saturation the saturation component, between 0 and 1
     * @param value the value component, between 0 and 1
     * @param alpha the alpha component, between 0 and 255
     * @return a {@code Color} composed of the specified hue, saturation,
     *     value, and alpha components
     */
    public static Color hsv(float hue, float saturation, float value,
            int alpha)
    {
        return fromRawColor(android.graphics.Color.HSVToColor(alpha,
                new float[] { hue, saturation, value }));
    }


    // ----------------------------------------------------------
    /**
     * Gets a {@code Color} that represents an Android color integer.
     *
     * @param rawColor the Android color integer
     * @return a {@code Color} that represents the specified Android color
     *     integer
     */
    public static Color fromRawColor(int rawColor)
    {
        return new Color(rawColor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the random color.
     *
     * @return a random color
     */
    public static Color getRandomColor()
    {
        Random gen = Random.generator();
        return fromRawColor(android.graphics.Color.rgb(
            gen.nextInt(256), gen.nextInt(256), gen.nextInt(256)));
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Returns true if this color is opaque (it has an alpha of 255), or false
     * if it has an alpha less than 255.
     * </p><p>
     * Notice that {@link #isOpaque()} is not the strict opposite of
     * {@link #isTransparent()}. Colors with alpha between 1 and 254 will
     * return false for both.
     * </p>
     *
     * @return true if the color is opaque, false if it is not
     */
    public boolean isOpaque()
    {
        return android.graphics.Color.alpha(rawColor) == 255;
    }


    // ----------------------------------------------------------
    /**
     * <p>
     * Returns true if this color is transparent (it has an alpha of 0), or
     * false if it has an alpha greater than 0.
     * </p><p>
     * Notice that {@link #isTransparent()} is not the strict opposite of
     * {@link #isOpaque()}. Colors with alpha between 1 and 254 will return
     * false for both.
     * </p>
     *
     * @return true if the color is transparent, false if it is not
     */
    public boolean isTransparent()
    {
        return android.graphics.Color.alpha(rawColor) == 0;
    }


    // ----------------------------------------------------------
    /**
     * Gets the red component of the receiver.
     *
     * @return the red component of the receiver, between 0 and 255
     */
    public int red()
    {
        return android.graphics.Color.red(rawColor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the green component of the receiver.
     *
     * @return the green component of the receiver, between 0 and 255
     */
    public int green()
    {
        return android.graphics.Color.green(rawColor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the blue component of the receiver.
     *
     * @return the blue component of the receiver, between 0 and 255
     */
    public int blue()
    {
        return android.graphics.Color.blue(rawColor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the alpha component of the receiver.
     *
     * @return the alpha component of the receiver, between 0 and 255
     */
    public int alpha()
    {
        return android.graphics.Color.alpha(rawColor);
    }


    // ----------------------------------------------------------
    /**
     * Gets the hue component of the receiver in the HSV color model.
     *
     * @return the hue component of the receiver, between 0 (inclusive) and
     *     360 (exclusive)
     */
    public float hue()
    {
        computeHSVIfNecessary();
        return hsv[0];
    }


    // ----------------------------------------------------------
    /**
     * Gets the saturation component of the receiver in the HSV color model.
     *
     * @return the saturation component of the receiver, between 0 and 1
     */
    public float saturation()
    {
        computeHSVIfNecessary();
        return hsv[1];
    }


    // ----------------------------------------------------------
    /**
     * Gets the value component of the receiver in the HSV color model.
     *
     * @return the value component of the receiver, between 0 and 1
     */
    public float value()
    {
        computeHSVIfNecessary();
        return hsv[2];
    }


    // ----------------------------------------------------------
    /**
     * Gets a new {@code Color} that is a brighter version of the receiver. The
     * alpha value will remain the same.
     *
     * @return a {@code Color} that is a brighter version of the receiver
     */
    public Color brighter()
    {
        int r = red();
        int g = green();
        int b = blue();
        int alpha = alpha();

        int i = (int) (1.0 / (1.0 - FACTOR));

        if (r == 0 && g == 0 && b == 0)
        {
            return rgb(i, i, i, alpha);
        }

        if (r > 0 && r < i) r = i;
        if (g > 0 && g < i) g = i;
        if (b > 0 && b < i) b = i;

        return rgb(Math.min((int) (r / FACTOR), 255),
                   Math.min((int) (g / FACTOR), 255),
                   Math.min((int) (b / FACTOR), 255),
                   alpha);
    }


    // ----------------------------------------------------------
    /**
     * Gets a new {@code Color} that is a darker version of the receiver. The
     * alpha value will remain the same.
     *
     * @return a {@code Color} that is a darker version of the receiver
     */
    public Color darker()
    {
        return rgb(Math.max((int) (red() * FACTOR), 0),
                   Math.max((int) (green() * FACTOR), 0),
                   Math.max((int) (blue() * FACTOR), 0),
                   alpha());
    }


    // ----------------------------------------------------------
    /**
     * Gets a new {@code Color} that is chromatically the same as the receiver
     * (it has the same red, green, and blue components), but with a different
     * alpha component.
     *
     * @param alpha the desired alpha component of the new color
     * @return a {@code Color} that is chromatically the same as the receiver
     *     but with a different alpha component
     */
    public Color withAlpha(int alpha)
    {
        return rgb(red(), green(), blue(), alpha);
    }


    // ----------------------------------------------------------
    /**
     * Gets the Android color integer that corresponds to the receiver.
     *
     * @return the Android color integer that corresponds to the receiver
     */
    public int toRawColor()
    {
        return rawColor;
    }


    // ----------------------------------------------------------
    /**
     * Gets a value indicating whether the receiving {@code Color} is equal to
     * the specified object. A {@code Color} is equal to another object if the
     * other object is not null, a {@code Color}, and has identical red, green,
     * blue, and alpha components as the receiver.
     *
     * @param other the other object
     * @return true if the receiver is equal to the other object, or false if
     *     it is not
     */
    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Color)
        {
            return (rawColor == ((Color) other).rawColor);
        }
        else
        {
            return false;
        }
    }


    // ----------------------------------------------------------
    /**
     * Returns a hash code value for the object.
     *
     * @return a hash code value for the object
     */
    @Override
    public int hashCode()
    {
        return rawColor;
    }


    // ----------------------------------------------------------
    /**
     * Gets a human-readable string representation of the color, in the format
     * "Color([red], [green], [blue], [alpha])".
     *
     * @return a human-readable string representation of the color
     */
    @Override
    public String toString()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Color(");
        buffer.append(android.graphics.Color.red(rawColor));
        buffer.append(", ");
        buffer.append(android.graphics.Color.green(rawColor));
        buffer.append(", ");
        buffer.append(android.graphics.Color.blue(rawColor));
        buffer.append(", ");
        buffer.append(android.graphics.Color.alpha(rawColor));
        buffer.append(")");

        return buffer.toString();
    }


    // ----------------------------------------------------------
    /**
     * Computes and caches the HSV components of this color, if they have not
     * been computed already.
     */
    private void computeHSVIfNecessary()
    {
        if (hsv == null)
        {
            hsv = new float[3];
            android.graphics.Color.colorToHSV(rawColor, hsv);
        }
    }
}
