package com.markodevcic.newsreader

import com.markodevcic.newsreader.util.SchedulerProvider
import rx.Scheduler
import rx.schedulers.Schedulers

class TestSchedulerProvider : SchedulerProvider {
	override val io: Scheduler
		get() = Schedulers.immediate()
	override val ui: Scheduler
		get() = Schedulers.immediate()
	override val computation: Scheduler
		get() = Schedulers.immediate()
	override val immediate: Scheduler
		get() = Schedulers.immediate()
}