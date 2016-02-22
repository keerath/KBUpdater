package com.imaginea.kodebeagle

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ListBranchCommand.ListMode
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.{ObjectId, Repository}
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.CanonicalTreeParser

import scala.collection.JavaConversions._
import scala.util.Try

private class GitDiffHelper extends DiffHelper {

  override def getDiffs(repoPath: String): Option[List[DiffEntry]] = {
    val gitRepoPath = s"$repoPath${File.separator}.git"
    val mayBeRepository = buildRepository(gitRepoPath)
    mayBeRepository.isSuccess match {
      case true => val repository = mayBeRepository.get
        val HEAD = "HEAD^{tree}"
        val oldHead = repository.resolve(HEAD)
        implicit val git = new Git(repository)
        invokePullCommand match {
          case true => val newHead = repository.resolve(HEAD)
            getDiffs(oldHead, newHead).toOption
          case false => None
        }
      case false => None
    }
  }

  private def getDiffs(oldHead: ObjectId, newHead: ObjectId)
                      (implicit git: Git): Try[List[DiffEntry]] = {
    val repository = git.getRepository
    val reader = repository.newObjectReader()
    val oldTree = new CanonicalTreeParser()
    oldTree.reset(reader, oldHead)
    val newTree = new CanonicalTreeParser()
    newTree.reset(reader, newHead)
    Try(git.diff().setNewTree(newTree).setOldTree(oldTree).call().toList)
  }

  private def buildRepository(gitRepoPath: String): Try[Repository] = {
    val repositoryBuilder = new FileRepositoryBuilder
    Try(repositoryBuilder.setMustExist(true).setGitDir(new File(gitRepoPath)).build)
  }

  private def invokePullCommand(implicit git: Git): Boolean = {
    val mayBeRemoteAndBranch = getRemoteAndBranch
    val repoPath = git.getRepository.getDirectory.getName.stripSuffix("/.git")
    mayBeRemoteAndBranch match {
      case Some(x) => val pullResult = git.pull().setRemote(x._1).setRemoteBranchName(x._2).call()
        if (pullResult.isSuccessful) {
          log.info(s"Pull successful for $repoPath")
          true
        } else {
          log.error(s"Pull failed for $repoPath")
          false
        }
      case None => log.error(s"No Remote-Ref found for $repoPath")
        false
    }
  }

  private def getRemoteAndBranch(implicit git: Git): Option[(String, String)] = {
    val mayBeRefs = Try(git.branchList().setListMode(ListMode.REMOTE).call())
    mayBeRefs.isSuccess match {
      case true => mayBeRefs.get.headOption match {
        case Some(ref) =>
          val repository = git.getRepository
          val refName = ref.getName
          Some(repository.getRemoteName(refName), repository.shortenRemoteBranchName(refName))
        case None => None
      }
      case false => None
    }
  }
}
