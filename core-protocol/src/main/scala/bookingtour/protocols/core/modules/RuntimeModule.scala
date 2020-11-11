package bookingtour.protocols.core.modules

import java.util.concurrent.{Executors, ThreadFactory}
import java.util.concurrent.atomic.AtomicLong

import zio.internal.tracing.TracingConfig

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

/**
  * © Alexey Toroshchin 2019.
  */
final class RuntimeModule private (val parN: Int, val firstRun: Boolean, val blockingEC: ExecutionContext)(
    implicit val zioRuntime: zio.Runtime[zio.ZEnv]
)

object RuntimeModule {
  private final val tc: TracingConfig = TracingConfig(
    traceExecution = true,
    traceEffectOpsInExecution = true,
    traceStack = true,
    executionTraceLength = 3,
    stackTraceLength = 1,
    ancestryLength = 1,
    ancestorExecutionTraceLength = 1,
    ancestorStackTraceLength = 1
  )
  final def apply()(
      implicit baseModule: BaseModule,
      runtime: zio.Runtime[zio.ZEnv]
  ): RuntimeModule = {
    import baseModule._
    val parN     = appConfig.getInt(s"operation.parN")
    val firstRun = appConfig.getBoolean(s"operation.firstRun")
    val blockingEC: ExecutionContextExecutor =
      ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new ThreadFactory {
        private val counter = new AtomicLong(0L)

        def newThread(r: Runnable): Thread = {
          val th = new Thread(r)
          th.setName(s"blocking-io-thread-${counter.getAndIncrement.toString}")
          th.setDaemon(true)
          th
        }
      }))
    new RuntimeModule(parN, firstRun, blockingEC)
  }

  final def default(
      traceEnabled: Boolean
  )(implicit baseModule: BaseModule): RuntimeModule = {
    import baseModule._
    val parN     = appConfig.getInt(s"operation.parN")
    val firstRun = appConfig.getBoolean(s"operation.firstRun")
    implicit val z: zio.Runtime[zio.ZEnv] = if (traceEnabled) {
      zio.Runtime.default.withTracingConfig(tc)
    } else {
      zio.Runtime.default.withTracingConfig(TracingConfig.disabled)
    }
    val blockingEC: ExecutionContextExecutor =
      ExecutionContext.fromExecutor(Executors.newCachedThreadPool(new ThreadFactory {
        private val counter = new AtomicLong(0L)

        def newThread(r: Runnable): Thread = {
          val th = new Thread(r)
          th.setName(s"blocking-io-thread-${counter.getAndIncrement.toString}")
          th.setDaemon(true)
          th
        }
      }))
    new RuntimeModule(parN, firstRun, blockingEC)
  }
}
