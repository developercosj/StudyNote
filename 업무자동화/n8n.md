N8N 

# Built-in nodes vs Community nodes 

Built-in nodes (내장 노드) 
제품/플랫폼에 기본으로 포함된 노드
Community nodes (커뮤니티 노드)
사용자/커뮤니티가 만들어서 공유한 노드



# HTTP Request node 

모든 형태의 노드를 제공할 수 없기 때문에 HTTP Request node 로 Custom 하여 API Call 가능 


# Credential-only 노드 

n8n 은 credential-only(인증 전용) 노드를 포함한다. 


# Build a declarative-style node

declarative-style node  (선언적 스타일 노드)
JavaScript, TypeScript, REST APIs, git 

APOD[Astronomy Picture or the Day] and Mars Rover Photos 




# Build an AI chat agent 

Ai agent builds on Llms (Large Language Models)
AI Agent Node 



# Data structure of n8n
n8n nodes function as an Extract, Transform, Load tool 
Data sent from one node to another is sent as an array of JSON objects 

## Code node 
you can use expressions to reference data from other nodes, you can also use some methods and variables in the Code node 


## Transforming data 
incoming data from some nodes may have a different data structure that the one used in n8n 
you need to transform the data 
common operations for data transformation are: 
1. creating multiple items from one item 
2. creating a single item from multiple items 

- Split Out node to separate a single data item containing a list into multiple items 
- Aggregate node to take separate items, or portions of them, and group them together into individual items

아래와 같이 반환할 수도 있다. 
let items = $input.all();
return items[0].json.workEmail.map(item => {
    return {
        json: item 
    }
})








































