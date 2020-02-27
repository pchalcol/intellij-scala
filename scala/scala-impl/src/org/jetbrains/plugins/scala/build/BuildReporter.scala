package org.jetbrains.plugins.scala.build

import java.io.File

import com.intellij.build._
import com.intellij.build.events._
import org.jetbrains.annotations.Nls
import org.jetbrains.plugins.scala.build.BuildMessages.EventId

trait BuildReporter {

  /** Beginning of reporting. */
  def start(): Unit

  /** End of reporting. */
  def finish(messages: BuildMessages): Unit

  /** Reporting ended due to error. */
  def finishWithFailure(err: Throwable): Unit

  /** Reporting ends due to task being canceled. */
  def finishCanceled(): Unit

  /** Show warning message. */
  def warning(@Nls message: String, position: Option[FilePosition]): Unit

  /** Show error message. */
  def error(@Nls message: String, position: Option[FilePosition]): Unit

  /** Show message. */
  def info(@Nls message: String, position: Option[FilePosition]): Unit

  /** Clear any messages associated with file. */
  def clear(file: File): Unit

  /** Print message to log. */
  def log(@Nls message: String): Unit

  /** Start a subtask. */
  def startTask(eventId: EventId, parent: Option[EventId], @Nls message: String, time: Long = System.currentTimeMillis()): Unit

  /** Show progress on a subtask. */
  def progressTask(eventId: EventId, total: Long, progress: Long, unit: String, @Nls message: String, time: Long = System.currentTimeMillis()): Unit

  /** Show completion of a subtask. */
  def finishTask(eventId: EventId, @Nls message: String, result: EventResult, time: Long = System.currentTimeMillis()): Unit
}
