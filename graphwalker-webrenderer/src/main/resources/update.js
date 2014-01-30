/*
 * #%L
 * GraphWalker Web Renderer
 * %%
 * Copyright (C) 2011 - 2014 GraphWalker
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */
var ws;
var modelsHash = {};

var transformprop = getsupportedprop([ "transform", "MozTransform",
		"webkitTransform" ]);
var transformoriginprop = getsupportedprop([ "transformOrigin",
		"MozTransformOrigin", "webkitTransformOrigin" ]);
var boxshadowprop = getsupportedprop([ "boxShadow", "MozBoxShadow",
		"webkitBoxShadow" ]);

window.onload = function connect() {
	var inputField = document.getElementById("serverAdr");
	port = window.location.port;
        ipAddr = window.location.hostname;
	var wsconnection = "ws://" + ipAddr + ":" + port + "/graphwalker";
	ws = new WebSocket(wsconnection); // "ws://nisse:8080/hellowebsocket"

	ws.onopen = function() {};
	ws.onclose = function() {};

	ws.onmessage = function(msg) {
		handleMessage(msg.data);
	};
	var mC = document.getElementById("modelsContainer");
	while (mC.firstChild) {
		mC.removeChild(mC.firstChild);
	}
}

function handleMessage(data) {
	var message = JSON.parse(data);
	if (message.type == "initial") {
		message.models.forEach(function(model) {
			var m = new modelObject(model);
			m.drawGraph();
			modelsHash[model.id] = m;
		});
	} else if (message.type == "update") {
		update(message.models);
	}
}

function update(models) {
	var activeElements = document.getElementsByClassName("active");
	for ( var i = 0; i < activeElements.length; i++) {
		activeElements[i].classList.add("visited");
		activeElements[i].classList.remove("active");

    }

	models.forEach(function(model) {
		var modelId = model.id;
        highlightContainer(modelId);
		if (model.nodes) {
			model.nodes.forEach(function(n) {
				var node = document.getElementById(n.id + "_" + modelId);
				node.style.background = "";
				node.classList.remove("unvisited");
				node.classList.add(n.state);
				if (n.state == "active") {
					modelsHash[modelId].centerOnElement(node);
				}
			});
		}
		if (model.edges) {
			model.edges.forEach(function(e) {
				var edge = document.getElementById(e.id + "_" + modelId);
				edge.classList.remove("unvisited");
				edge.classList.add(e.state);
                modelsHash[modelId].centerOnElement(edge);
                if (e.state == "active") {
                    modelsHash[modelId].centerOnElement(edge);
                }
			});
		}
		if (model.variables) {
			var new_tbody = document.createElement("tbody");
			new_tbody.id = "data_body_" + modelId;
			model.variables.forEach(function(item) {
				var row = document.createElement("tr");
				var key = document.createElement("td");
				key.innerHTML = item.name + ":";
				var value = document.createElement("td");
				value.innerHTML = item.value;
				value.id = item.name + "_" + modelId;
				row.appendChild(key);
				row.appendChild(value);
				new_tbody.appendChild(row);
			});
		    vtable = document.getElementById("data_table_" + modelId);
			old_tbody = document.getElementById("data_body_" + modelId);
			vtable.replaceChild(new_tbody, old_tbody)
		}
	});
}

function highlightContainer(id) {
    var containers = document.getElementsByClassName("container");
    for(var i = 0; i < containers.length; i++) {
        if (containers[i].id == "container_" + id) {
            containers[i].classList.add("activemodel");
        } else {
            containers[i].classList.remove("activemodel");
        }
    }
}

function modelObject(data) {
	var graph = data;
	var m_id = graph.id;
    var modelName = graph.name;
	var modelsContainer = document.getElementById("modelsContainer");
	var container = document.createElement("div");
	container.id = "container_" + m_id;
	container.classList.add("container")
	modelsContainer.appendChild(container);
	var graphDiv = document.createElement("div");
	graphDiv.id = "model_" + m_id;
	graphDiv.classList.add("model");
	container.appendChild(graphDiv);

	var panning = false;
	var mousedownX = 0;
	var mousedownY = 0;
	var transX = 0;
	var transY = 0;
	var scale = 1;
	container.onmousedown = function(e) {
		panning = true;
		mousedownX = e.clientX;
		mousedownY = e.clientY;
		graphDiv.classList.remove("animated");
	};
	container.onmouseup = function(e) {
		panning = false;
		transX += e.clientX - mousedownX;
		transY += e.clientY - mousedownY;
	};
	container.onmousemove = handlePanning;

	var minY = 1e9;
	var minX = 1e9;

	this.centerOnElement = function(element) {
		graphDiv.classList.add("animated");
		transX = element.parentNode.offsetWidth / 2
				- (element.offsetLeft + element.offsetWidth / 2);
		transY = element.parentNode.offsetHeight / 2
				- (element.offsetTop + element.offsetHeight / 2);
		graphDiv.style[transformprop] = "translate(" + transX + "px," + transY
				+ "px) scale(" + scale + ")";
		graphDiv.style[transformoriginprop] = (element.offsetLeft + element.offsetWidth / 2)
				+ "px " + (element.offsetTop + element.offsetHeight / 2) + "px";
	}

	this.drawGraph = function() {
		var zoomControls = document.createElement("div");
		zoomControls.classList.add("zoomdiv");
		var zIn = document.createElement("input");
		zIn.type = "button";
		zIn.onclick = zoom;
		zIn.classList.add("zoominput");
		zIn.value = "+";
		var zOut = document.createElement("input");
		zOut.type = "button";
		zOut.onclick = zoom;
		zOut.classList.add("zoominput");
		zOut.value = "-";
		zoomControls.appendChild(zIn);
		zoomControls.appendChild(zOut);
		container.appendChild(zoomControls);

		var vtable = document.createElement("table");
		vtable.id = "data_table_" + m_id;
		vtable.classList.add("variables");
		var vbody = document.createElement("tbody");
		vbody.id = "data_body_" + m_id;
		graph.variables.forEach(function(item) {
			var row = document.createElement("tr");
			var key = document.createElement("td");
			key.innerHTML = item.name + ":";
			var value = document.createElement("td");
			value.innerHTML = item.value;
			value.id = item.name + "_" + m_id;
			row.appendChild(key);
			row.appendChild(value);
			vbody.appendChild(row);
		});
		vtable.appendChild(vbody);
		container.appendChild(vtable);

        var nameSpan = document.createElement("span");
        nameSpan.classList.add("modelname");
        if (modelName) {
            nameSpan.innerHTML = modelName;
        }
        container.appendChild(nameSpan);

		graph.nodes.forEach(function(item) {
			if (item.geometry.y < minY)
				minY = item.geometry.y;
			if (item.geometry.x < minX)
				minX = item.geometry.x;
		});

		graph.edges.forEach(function(edge) {
			edge.path.points.forEach(function(point) {
				if (point.y < minY)
					minY = point.y;
				if (point.x < minX)
					minX = point.x;
			});
		});

		nodes = {};
		minX = minX - 50;
		minY = minY - 50;

		graph.nodes.forEach(function(item) {
			var div = document.createElement("div");
			div.style.width = item.geometry.width + "px";
			div.style.height = item.geometry.height + "px";
			div.style.left = item.geometry.x - minX + "px";
			div.style.top = item.geometry.y - minY + "px";
			div.style[boxshadowprop] = "5px 5px 5px #888";
			div.style.backgroundColor = item.color;
			div.style.background = "-moz-linear-gradient(top , "
					+ LightenDarkenColor(item.color, 100) + ", " + item.color
					+ ")";
			div.style.background = "-webkit-linear-gradient(top, "
					+ LightenDarkenColor(item.color, 100) + ", " + item.color
					+ ")";
			div.className = "node " + item.state;
			div.id = item.id + "_" + m_id;
			var par = document.createElement("p");
			par.classList.add("label");
			par.innerHTML = item.label.replace(/,/g, '<br>');
			div.appendChild(par);
			graphDiv.appendChild(div);
			nodes[div.id] = item;
		});
		graph.edges.forEach(function(item) {
			var source = nodes[item.source + "_" + m_id];
			var target = nodes[item.target + "_" + m_id];
			add_arrow(item, source, target);
		});
	}

	function add_arrow(item, source, target) {
		var from = {
			x : source.geometry.x + source.geometry.width / 2 + item.path.sx,
			y : source.geometry.y + source.geometry.height / 2 + item.path.sy
		};
		var start = {
			x : from.x,
			y : from.y
		};
		var to = {
			x : target.geometry.x + target.geometry.width / 2 + item.path.tx,
			y : target.geometry.y + target.geometry.height / 2 + item.path.ty
		};
		var edge = document.createElement("div");
		edge.id = item.id + "_" + m_id;
		edge.style.left = from.x - minX + "px";
		edge.style.top = from.y - minY + "px";
		edge.style.position = "absolute";
		edge.className = "edge " + item.state;

		var label = document.createElement('p');
		label.classList.add("label");
		label.style.left = item.label.x + "px";
		label.style.top = item.label.y + "px";
		label.innerHTML = item.label.label;
		label.style.position = "absolute";
		edge.appendChild(label);

		item.path.points.forEach(function(point) {
			var d = {
				x : point.x - from.x,
				y : point.y - from.y
			};
			var length = Math.sqrt(d.x * d.x + d.y * d.y);
			var angle = Math.atan2(d.y, d.x) - Math.PI / 2;
			line = document.createElement("div");
			line.style.height = length + "px";
			line.style.left = from.x - start.x + "px";
			line.style.top = from.y - start.y + "px";
			line.style[transformprop] = "rotate(" + angle + "rad)";
			line.className = "line";
			edge.appendChild(line);
			from = point;
		});

		var d = {
			x : to.x - from.x,
			y : to.y - from.y
		};
		var length = Math.sqrt(d.x * d.x + d.y * d.y);
		var angle = Math.atan2(d.y, d.x) - Math.PI / 2;
		line = document.createElement("div");
		line.style.height = length + "px";
		line.style.left = from.x - start.x + "px";
		line.style.top = from.y - start.y + "px";
		line.style[transformprop] = "rotate(" + angle + "rad)";
		line.className = "line";
		var head = document.createElement("div");
		head.className = "arrow-down";
		line.appendChild(head);
		edge.appendChild(line);
		graphDiv.appendChild(edge);

	}

	function handleVerticeClick(e) {
		var className = e.srcElement.parentNode.className;
		if (className.indexOf("active") > -1) {
			ws
					.send('{"type":"update", "models":[{"id": "test", "nodes":[{"id":"'
							+ e.target.parentNode.id.replace("_" + m_id, "")
							+ '", "state":"failed"}]}]}');
		} else {
			ws
					.send('{"type":"update", "models":[{"id": "test", "nodes":[{"id":"'
							+ e.target.parentNode.id.replace("_" + m_id, "")
							+ '", "state":"active"}]}]}');
		}
	}

	function handlePanning(e) {
		if (panning) {
			graphDiv.style[transformprop] = "translate("
					+ (transX + e.clientX - mousedownX) + "px,"
					+ (transY + e.clientY - mousedownY) + "px) scale(" + scale
					+ ")";
		}
	}

	function zoom(e) {
		var siblings = e.target.parentNode.parentNode.childNodes;
		for ( var i = 0; i < siblings.length; i++) {
			if (siblings[i].classList.contains("model")) {
				if (e.target.value == "+") {
					scale = scale * 1.1;
				} else {
					scale = scale / 1.1;
				}
				siblings[i].style[transformprop] = "translate(" + transX
						+ "px," + transY + "px) scale(" + scale + ")";
			}
		}
	}
}

function getsupportedprop(proparray) {
	var root = document.documentElement //reference root element of document
	for ( var i = 0; i < proparray.length; i++) { //loop through possible properties
		if (typeof root.style[proparray[i]] == "string") { //if the property value is a string (versus undefined)
			return proparray[i] //return that string
		}
	}
}

function LightenDarkenColor(col, amt) {
	var usePound = false;
	if (col[0] == "#") {
		col = col.slice(1);
		usePound = true;
	}
	var num = parseInt(col, 16);
	var r = (num >> 16) + amt;
	if (r > 255)
		r = 255;
	else if (r < 0)
		r = 0;
	var b = ((num >> 8) & 0x00FF) + amt;
	if (b > 255)
		b = 255;
	else if (b < 0)
		b = 0;
	var g = (num & 0x0000FF) + amt;
	if (g > 255)
		g = 255;
	else if (g < 0)
		g = 0;
	return (usePound ? "#" : "") + (g | (b << 8) | (r << 16)).toString(16);
}