你可以通过Sketch的SLogTracker接口跟踪所有的运行日志，然后将其保存到SD卡，方便后续跟踪分析

首先你需要实现SLogTracker接口（可参考sample app中的[SampleLogTracker.java](../../sample/src/java/me/xiaopan/sketchsample/SampleLogTracker.java)）

然后通过SLog.setLogTracker(SLogTracker)方法应用即可

你还可以通过[SLogType](../../sample/src/java/me/xiaopan/sketchsample/SLogType.java)类控制只输出不同类型的日志，方便只看需要的日志