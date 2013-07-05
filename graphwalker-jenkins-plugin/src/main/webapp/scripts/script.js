

var elements = [
    new Treemap.Group([new Treemap.Element(21, 20)
        , new Treemap.Element(20, 20)
        , new Treemap.Element(19, 20)
        , new Treemap.Element(18, 20)
        , new Treemap.Element(1, 20)
    ], 1),
    new Treemap.Group([new Treemap.Element(16, 20)
        , new Treemap.Element(15, 20)
        , new Treemap.Element(14, 20)
        , new Treemap.Element(13, 20)
        , new Treemap.Element(12, 20)
        , new Treemap.Element(1, 20)
    ], 2),
    new Treemap.Group([new Treemap.Element(10, 20)
        , new Treemap.Element(9, 20)
        , new Treemap.Element(2, 20)
        , new Treemap.Element(3, 20)
        , new Treemap.Element(1, 20)
        , new Treemap.Element(1, 20)
        , new Treemap.Element(1, 20)
        , new Treemap.Element(1, 20)
        , new Treemap.Element(1, 20)
        , new Treemap.Element(1, 20)
    ], 3)
];

function draw() {
    var context = document.getElementById('canvas').getContext('2d');
    context.canvas.width  = window.innerWidth;
    context.canvas.height = window.innerHeight;
    Treemap.draw(elements, context);
}

$(window).on("load", function(event) {
  draw();
});

$(window).on("resize", function(event) {
  draw();
});

