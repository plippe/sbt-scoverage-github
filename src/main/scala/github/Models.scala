package com.github.plippe.github

case class PullRequest(
    number: Int,
    head: Head
)

case class Head(sha: String)

case class Comment(body: String)
