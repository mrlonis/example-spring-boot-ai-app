import org.springframework.cloud.contract.spec.Contract

[
	// GET /ai/embedding?message=hello
	Contract.make {
		name 'generate_embedding'
		description 'Returns embedding for a message'
		request {
			method 'GET'
			urlPath('/ai/embedding') {
				queryParameters {
					parameter 'message': value(consumer('hello'), producer('hello'))
				}
			}
		}
		response {
			status 200
			headers { contentType(applicationJson()) }
			body(embedding: [metadata: [model: 'stub'], result: [output: [0.0]]])
			bodyMatchers { jsonPath('$.embedding', byType()) }
		}
	},
	// GET /ai/vector?query=spring
	Contract.make {
		name 'vector_similarity_search'
		description 'Returns list of documents for query'
		request {
			method 'GET'
			urlPath('/ai/vector') {
				queryParameters {
					parameter 'query': value(consumer('spring'), producer('spring'))
				}
			}
		}
		response {
			status 200
			headers { contentType(applicationJson()) }
			body([[ id: anyUuid() ]])
			bodyMatchers { jsonPath('$', byType()) }
		}
	}
]
