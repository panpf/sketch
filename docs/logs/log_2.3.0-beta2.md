修复BUG：
>* [#16](https://github.com/xiaopansky/sketch/issues/16) 修复由于在ImageView onAttachedToWindow的时候重新创建了ImageZoomer导致先前往ImageZoomer中设置的各种监听失效的BUG，此BUG的直接表现就是在Activity中无法使用分块显示超大图功能（在Fragment中并不会触发onAttachedToWindow所以在Fragment中看起来一切正常）

优化：
>* 优化了LargeImageView的暂停功能，并且pause()和resume()方法合为setPause(boolean)一个了