有的时候碰到被旋转的图片，看着很费劲。图片查看器支持了旋转还好说，要是不支持可就蛋疼了。

现在Sketch支持识别图片方向并且自动旋转，看起来就跟图片原本就是正常的一样，并且超大图也能使用，但仅支持jpeg类型的图片

### 使用方法：

#### Options：

```java
LoadOptions options = ...;
options.setCorrectImageOrientation(true);
```

or

```java
DisplayOptions options = ...;
options.setCorrectImageOrientation(true);
```

#### Helper：

```java
Sketch.with(context).load(...).correctImageOrientation().commit();
```

or

```java
Sketch.with(context).display(...).correctImageOrientation().commit();
```