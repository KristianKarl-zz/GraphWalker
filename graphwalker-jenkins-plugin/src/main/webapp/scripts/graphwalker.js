(function(graphwalker, undefined) {
    "use strict";

    graphwalker.Stage = (function() {
        function Stage(canvasId) {

            this.canvas = document.getElementById(canvasId);
            this.context = this.canvas.getContext("2d");
            this.displayObject = new graphwalker.DisplayObject(this.canvas);

            this.addChild = function(child) {
                this.displayObject.addChild(child);
            }

            this.setSize = function(width, height) {
                this.displayObject.setSize(width, height);
            }

            this.update = function() {
                this.displayObject.draw(context);

                //this.context.beginPath();
                //this.context.rect(0, 0, this.canvas.width, this.canvas.height);
                //this.context.fillStyle = "black";
                //this.context.fill();
            }
        }
        return Stage;
    }());

    graphwalker.DisplayObject = (function(){
        function DisplayObject(object) {
            this.object = object;
            this.parent = null;
            this.childrenSet = {};

            this.addChildren = function(children) {

            }

            this.addChild = function(child) {
                if (child instanceof graphwalker.DisplayObject) {
                    if (child.parent === null) {
                        this.childrenSet[child] = true;
                        child.parent = this;
                    } else if (child.parent !== this) {
                        child.parent.removeChild(child);
                        this.childrenSet[child] = true;
                        child.parent = this;
                    }
                }
            }

            this.removeChildren = function(children) {

            }

            this.removeChild = function(child) {
                delete this.childrenSet[child];
            }

            this.contains = function(child) {
                while (child) {
                    if (child === this) {
                        return true;
                    }
                    child = child.parent;
                }
                return false;
            }

            this.setSize = function(width, height) {
                this.object.width = width;
                this.object.height = height;
            }

            this.getX = function() {
                return this.object.x;
            }

            this.getY = function() {
                return this.object.y;
            }

            this.getWidth = function() {
                return this.object.width;
            }

            this.getHeight = function() {
                return this.object.height;
            }

            this.draw = function(context) {
                var list = this.children.slice(0);
                for (var index = 0, length = list.length; index < length; index++) {
                    var child = list[i];

                }
            }
        }
        return DisplayObject;
    }());

    graphwalker.Shape = (function() {
        function Shape(x, y, width, height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        return Shape;
    }());

}(window.graphwalker = window.graphwalker || {}));