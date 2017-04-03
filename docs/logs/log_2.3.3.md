
重构：
* ImageInfo重命名为ImageAttrs，并将当中的key和uri移出，现在其定位为图片的属性
* ImageFormat重命名为ImageType，LargeImageViewer.getImageFormat()方法重命名为getImageType()
* ImageAttrs中新增orientation属性，存储图片旋转角度

新功能：
* load和display的Options以及Helper新增correctImageOrientation属性可让被旋转了的图片以正常方向显示，[点击了解更多](../wiki/correct_image_orientation.md)