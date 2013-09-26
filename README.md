todowell
========

Source code todo

## Workflow:

* at first push commit hook
  * get all contents
  * parse and store TODO lines

* at subsequent push commit hooks
  * get modified files
  * parse TODO lines
  * update database

## Getting all contents (using the api):

  * get all refs: 

	  `curl https://api.github.com/repos/mcaprari/botto/git/refs`

	  {
	    "ref": "refs/heads/snapshot",
	    "url": "https://api.github.com/repos/mcaprari/botto/git/refs/heads/snapshot",
	    "object": {
	      "sha": "77d2648b6d956f1173dadf456ffe1fc6e2b5cb97",
	      "type": "commit",
	      "url": "https://api.github.com/repos/mcaprari/botto/git/commits/77d2648b6d956f1173dadf456ffe1fc6e2b5cb97"
	    }
	  },

  * recursively get the contents of a ref

	  `curl https://api.github.com/repos/mcaprari/botto/git/trees/77d2648b6d956f1173dadf456ffe1fc6e2b5cb97\?recursive\=true`

	  {
	      "mode": "100644",
	      "type": "blob",
	      "sha": "a153391fd57dfb1e8c3c5f25e21e70e65fe3008a",
	      "path": "botto-annotations/src/main/java/botto/xmpp/annotations/ConnectionInfo.java",
	      "size": 104,
	      "url": "https://api.github.com/repos/mcaprari/botto/git/blobs/a153391fd57dfb1e8c3c5f25e21e70e65fe3008a"
	  }

  * for each file, get the contents:

  	`curl -v -H Accept:application/vnd.github.v3.raw https://api.github.com/repos/mcaprari/botto/contents/botto-annotations/src/main/java/botto/xmpp/annotations/ConnectionInfo.java\?ref\=snapshot`

## Getting all contents (using JGit or git commandline):

* http://stackoverflow.com/questions/6395123/jgit-repository-browser?rq=1
* https://github.com/eclipse/jgit/blob/master/org.eclipse.jgit/src/org/eclipse/jgit/internal/storage/dfs/InMemoryRepository.java


## Store extracted json in S3:

https://s3.amazonaws.com/giddone/mcaprari/botto/master







  
