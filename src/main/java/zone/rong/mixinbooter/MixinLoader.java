package zone.rong.mixinbooter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface MixinLoader {

    // Purely a marker annotation. Use it in classes and said classes will be instantiated
    // Make sure there is an empty-arg constructor as LoaderMixin will call newInstance on it.
    // Feel free to do any Mixin related things in the constructor. But, most importantly, add (mod mixin) configs there.

}
