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

// -------------------------------------------------------------------------
/**
 * Defines how an animation repeats itself.
 *
 * @author Tony Allevato
 */
public enum RepeatMode
{
    //~ Constants .............................................................

    // ----------------------------------------------------------
    /**
     * The animation will not repeat; it will execute once from start to end.
     */
    NONE,


    // ----------------------------------------------------------
    /**
     * The animation will repeat by executing from start to end, then
     * instantaneously returning to the start and executing again, until the
     * animation is stopped (by calling {@link Shape#stopAnimation()}).
     */
    REPEAT,


    // ----------------------------------------------------------
    /**
     * The animation will oscillate by executing from start to finish, then
     * executing backward from end to start, and then executing again, until
     * the animation is stopped (by calling {@link Shape#stopAnimation()}).
     */
    OSCILLATE
}
