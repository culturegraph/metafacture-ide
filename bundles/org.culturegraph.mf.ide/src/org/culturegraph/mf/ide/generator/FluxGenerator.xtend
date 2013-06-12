/*
 * Copyright 2013 hbz, Fabian Steeg. Licensed under the Eclipse Public License 1.0
 */
package org.culturegraph.mf.ide.generator

import org.culturegraph.mf.ide.flux.Metaflow
import org.culturegraph.mf.ide.flux.Pipe
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.xtext.generator.IFileSystemAccess
import org.eclipse.xtext.generator.IGenerator

/**
 * Generate a Graphviz DOT representation for a Flux resource.
 * 
 * @author Fabian Steeg (fsteeg)
 */
class FluxGenerator implements IGenerator {
	override void doGenerate(Resource resource, IFileSystemAccess fsa) {
		fsa.generateFile(
			resource.name + ".dot",
			toDot(resource.contents.head as Metaflow)
		)
	}

	def toDot(Metaflow metaflow) '''
		digraph {
			«FOR mainflow : metaflow.flows»
				«processFlow(mainflow.flow.pipes.tail, mainflow.flow.pipes.head)»
				«FOR tee : mainflow.flow.tees»
					«FOR flow : tee.flows»
						«processFlow(flow.pipes, mainflow.flow.pipes.last)»
					«ENDFOR»
				«ENDFOR»
			«ENDFOR»
		}
	'''

	def processFlow(Iterable<Pipe> pipes, Pipe start) '''
		«var previous = start.hashCode.toString»
		«previous»[label="«start.id»"]
		«FOR pipe : pipes»
			«val current = pipe.hashCode»
			«previous» -> «current»
			«previous = current.toString»[label="«pipe.id»"]
		«ENDFOR»
	'''

	def name(Resource resource) {
		return resource.URI.lastSegment.split("\\.").get(0)
	}

	def id(Pipe pipe) {
		return pipe.qn.ids.join(".")
	}
}
