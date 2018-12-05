package com.github.plippe

package object implicits extends TryImplicits with EitherImplicits {

  // Scala 2.10 needs to import missing methods that are available in Scala 2.12
  // Call the following to avoid raising the 'Unused Import' error
  def avoidUnusedImport() = ()
}
