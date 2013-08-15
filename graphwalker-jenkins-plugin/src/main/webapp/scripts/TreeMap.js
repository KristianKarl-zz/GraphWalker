// Make a Treemap, using the squarified Treemap algorithm.
(function(Treemap, $, undefined) {
    "use strict";

    Treemap.Color = (function() {
        function Color() {

            this.rgbToHsl = function(r, g, b) {
                r /= 255, g /= 255, b /= 255;
                var max = Math.max(r, g, b), min = Math.min(r, g, b);
                var h, s, l = (max + min) / 2;

                if(max == min) {
                    h = s = 0;
                } else {
                    var d = max - min;
                    s = l > 0.5 ? d / (2 - max - min) : d / (max + min);
                    switch (max) {
                        case r: h = (g - b) / d + (g < b ? 6 : 0); break;
                        case g: h = (b - r) / d + 2; break;
                        case b: h = (r - g) / d + 4; break;
                    }
                    h /= 6;
                }

                return [h, s, l];
            }

            function hue2rgb(p, q, t) {
                if (t < 0) t += 1;
                if (t > 1) t -= 1;
                if (t < 1/6) return p + (q - p) * 6 * t;
                if (t < 1/2) return q;
                if (t < 2/3) return p + (q - p) * (2/3 - t) * 6;
                return p;
            }

            this.hslToRgb = function(h, s, l) {
                var r, g, b;

                if (s == 0) {
                    r = g = b = l;
                } else {
                    var q = l < 0.5 ? l * (1 + s) : l + s - l * s;
                    var p = 2 * l - q;
                    r = hue2rgb(p, q, h + 1/3);
                    g = hue2rgb(p, q, h);
                    b = hue2rgb(p, q, h - 1/3);
                }

                return [r * 255, g * 255, b * 255];
            }

            this.rgbToHsv = function(r, g, b) {
                r = r/255, g = g/255, b = b/255;
                var max = Math.max(r, g, b), min = Math.min(r, g, b);
                var h, s, v = max;

                var d = max - min;
                s = max == 0 ? 0 : d / max;

                if (max == min) {
                    h = 0;
                } else {
                    switch(max) {
                        case r: h = (g - b) / d + (g < b ? 6 : 0); break;
                        case g: h = (b - r) / d + 2; break;
                        case b: h = (r - g) / d + 4; break;
                    }
                    h /= 6;
                }

                return [h, s, v];
            }

            this.hsvToRgb = function(h, s, v) {
                var r, g, b;

                var i = Math.floor(h * 6);
                var f = h * 6 - i;
                var p = v * (1 - s);
                var q = v * (1 - f * s);
                var t = v * (1 - (1 - f) * s);

                switch (i % 6) {
                    case 0: r = v, g = t, b = p; break;
                    case 1: r = q, g = v, b = p; break;
                    case 2: r = p, g = v, b = t; break;
                    case 3: r = p, g = q, b = v; break;
                    case 4: r = t, g = p, b = v; break;
                    case 5: r = v, g = p, b = q; break;
                }

                return [r * 255, g * 255, b * 255];
            }

            this.transition = function(value, max, start, end) {
                return [
                    start[0] + (end[0] - start[0]) * value / max,
                    start[1] + (end[1] - start[1]) * value / max,
                    start[2] + (end[2] - start[2]) * value / max
                ]
            }

        }
        return Color;
    }());

    Treemap.Bounds = (function() {
        function Bounds(x, y, width, height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }());

    Treemap.Rectangle = (function() {
        function Rectangle(x, y, width, height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            this.getShortestEdge = function() {
                return Math.min(this.height, this.width);
            }

            this.getArea = function() {
                return this.height * this.width;
            }
        }
        return Rectangle;
    }());

    Treemap.Element = (function() {
        function Element(weight, data) {
            this.weight = weight;
            this.data = data;
            this.rectangle = undefined;
            this.normalizedWeight = undefined;
        }
        return Element;
    }());

    Treemap.Group = (function() {
        function Group(elements, data) {
            this.elements = elements.slice();
            this.data = data;
            this.weight = 0;
            for (var n in elements) {
                this.weight += elements[n].weight;
            }
            this.rectangle = undefined;
            this.normalizedWeight = undefined;
        }
        return Group;
    }());

    Treemap.Renderer = (function() {
        function Renderer(elements) {

            this.weights = toArray(elements, "weight");
            this.min = Math.min.apply(Math, this.weights);
            this.max = Math.max.apply(Math, this.weights);

            this.render = function(context, element) {
                if (element instanceof Treemap.Group) {
                    // todo: render group label

                    for (var n in element.elements) {
                        this.render(context, element.elements[n]);
                    }

                    var rectangle = element.rectangle;

                    var lineWidth = 4;
                    var halfLineWidth = lineWidth/2;

                    var topLineWidth = rectangle.y === 0 ? lineWidth : halfLineWidth;
                    var halfTopLineWidth = topLineWidth/2;
                    var rightLineWidth = rectangle.x+rectangle.width == context.canvas.width ? lineWidth : halfLineWidth;
                    var halfRightLineWidth = rightLineWidth/2;
                    var bottomLineWidth = rectangle.y+rectangle.height == context.canvas.height ? lineWidth : halfLineWidth;
                    var halfBottomLineWidth = bottomLineWidth/2;
                    var leftLineWidth = rectangle.x === 0 ? lineWidth : halfLineWidth;
                    var halfLeftLineWidth = leftLineWidth/2;

//console.log("w = "+context.canvas.width+" h ="+context.canvas.height);

                    context.beginPath();
                    context.lineJoin = 'miter';
                    context.lineCap = 'square';
                    context.strokeStyle = 'black';
                    context.lineWidth = topLineWidth;
                    context.moveTo(rectangle.x, rectangle.y+halfTopLineWidth);
                    context.lineTo(rectangle.x+rectangle.width-halfRightLineWidth, rectangle.y+halfTopLineWidth);
                    context.stroke();
                    context.lineWidth = rightLineWidth;
                    context.lineTo(rectangle.x+rectangle.width-halfRightLineWidth, rectangle.y+rectangle.height-halfBottomLineWidth);
                    context.stroke();
                    context.lineWidth = bottomLineWidth;
                    context.lineTo(rectangle.x+halfLeftLineWidth, rectangle.y+rectangle.height-halfBottomLineWidth);
                    context.stroke();
                    context.lineWidth = leftLineWidth;
                    context.lineTo(rectangle.x+halfLeftLineWidth, rectangle.y);
                    context.stroke();

                } else {
              /*
                    var rectangle = element.rectangle;
                    context.beginPath();
                    context.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);

                    //var ratio = (element.weight - this.min)/(this.max - this.min);
                    //var gradient = context.createLinearGradient(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
                    var color = new Treemap.Color();
                    var start = color.rgbToHsl(0, 255, 0);
                    var end = color.rgbToHsl(255, 0, 0);
                    var hsl = color.transition(element.weight, 25, start, end);
                    var rgb = color.hslToRgb(hsl[0], hsl[1], hsl[2]);

                    //var hsv2 = transition(element.weight - this.min, this.max - this.min, start_point, end_point);
                    //var rgb2 = hsv_to_rgb(hsv2);

                    //gradient.addColorStop(0, "rgb(" + Math.round(rgb1[0]) + "," + Math.round(rgb1[1]) + "," + Math.round(rgb1[2]) + ")");
                    //gradient.addColorStop(1-(1-ratio), "rgb(" + Math.round(rgb2[0]) + "," + Math.round(rgb2[1]) + "," + Math.round(rgb2[2]) + ")");
                    //context.fillStyle = gradient;
                    context.fillStyle = "rgb(" + Math.round(rgb[0]) + "," + Math.round(rgb[1]) + "," + Math.round(rgb[2]) + ")";
                    context.fill();

                    context.textBaseline = "top";
                    context.fillStyle = "black";
                    context.font = "bold 12px sans-serif";
                    context.fillText(element.weight+":"+rectangle.width+":"+rectangle.height, rectangle.x+10, rectangle.y+10);
                    //context.fillText(ratio, rectangle.x+10, rectangle.y+20);
                    //context.fillText(red+" "+green, rectangle.x+10, rectangle.y+30);

                    //context.lineWidth = 1;
                    //context.strokeStyle = 'white';
                    //context.stroke();
                */
                }
            }
        }
        return Renderer;
    }());

    Treemap.draw = function (elements, context, renderer) {
        renderer = renderer || new Treemap.Renderer(elements);
        var rectangle = new Treemap.Rectangle(0, 0, context.canvas.width, context.canvas.height);
        squarify(normalize(sort(elements, "weight"), rectangle).slice(), [], rectangle);
        for (var i = 0; i<elements.length; i++) {
            renderer.render(context, elements[i]);
        }
    };

    function squarify(children, row, rectangle) {
        while (0 < children.length) {
            var child = children.shift();
            if (isEmpty(row) || worst(rectangle, row) >= worst(rectangle, row, child)) {
                if (isEmpty(children)) {
                    row.push(child);
                    layoutrow(row, rectangle);
                } else {
                    row.push(child);
                    squarify(children, row, rectangle);
                }
            } else {
                var subrectangle = layoutrow(row, rectangle);
                var area = sumArray(toArray(row, "normalizedWeight"));
                children.unshift(child);
                squarify(children, [], subrectangle);
            }
        }
    }

    function layoutrow(row, rectangle) {
        var weights = toArray(row, "normalizedWeight");
        var sum = sumArray(weights);
        var x = rectangle.x;
        var y = rectangle.y;
        var maxX = rectangle.x + rectangle.width;
        var maxY = rectangle.y + rectangle.height;
        if (rectangle.width >= rectangle.height) {
            var width = sum / rectangle.height;
            for (var i = 0; i < row.length; i++) {
                var height = row[i].normalizedWeight / width;
                if (y+height > maxY || i+1 == row.length) {
                    height = maxY - y;
                }
                place(row[i], x, y, width, height);
                y = y + height;
            }
            return new Treemap.Rectangle(rectangle.x+width, rectangle.y, rectangle.width-width, rectangle.height);
        } else {
            var height = sum / rectangle.width;
            for (var i = 0; i < row.length; i++) {
                var width = row[i].normalizedWeight / height;
                if (x+width > maxX || i+1 == row.length) {
                    width = maxX - x;
                }
                place(row[i], x, y, width, height);
                x = x + width;
            }
            return new Treemap.Rectangle(rectangle.x, rectangle.y+height, rectangle.width, rectangle.height-height);
        }
    }

    function worst(rectangle, row, child) {
        var weights = toArray(row, "normalizedWeight");
        if (typeof child !== "undefined") {
            weights.push(child.normalizedWeight);
        }
        return ratio(weights, rectangle.getShortestEdge());
    }

    function ratio(weights, length) {
        var min = Math.min.apply(Math, weights);
        var max = Math.max.apply(Math, weights);
        var sum = sumArray(weights);
        return Math.max(Math.pow(length, 2) * max / Math.pow(sum, 2), Math.pow(sum, 2) / (Math.pow(length, 2) * min));
    }

    function place(element, x, y, width, height) {
//console.log(element.weight+"@["+x+","+y+"] w = "+width+" h = "+height);
        element.rectangle = new Treemap.Rectangle(x, y, width, height);
        if (element instanceof Treemap.Group) {
            squarify(normalize(sort(element.elements, "weight"), element.rectangle).slice(), [], element.rectangle);
        }
    }

    function normalize(elements, rectangle) {
        var weights = toArray(elements, "weight");
        var scale = rectangle.getArea() / sumArray(weights);
        for (var i = 0; i < elements.length; i++) {
            elements[i].normalizedWeight = scale * elements[i].weight;
        }
        return elements;
    }

    function sort(elements, property) {
        elements.sort(function (a, b) {
            return b[property] - a[property];
        });
        return elements;
    }

    function isEmpty(array) {
        return 0 === array.length;
    }

    function sumArray(array) {
        var total = 0;
        for (var i = 0; i < array.length; i++) {
            total += array[i];
        }
        return total;
    }

    function toArray(elements, property) {
        return elements.map(function (element) {
            return element[property];
        });
    }

}(window.Treemap = window.Treemap || {}, jQuery));



