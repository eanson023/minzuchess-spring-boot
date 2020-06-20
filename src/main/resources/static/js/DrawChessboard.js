var CanvasWidth = 800,
    CanvasHeight = 520,
	ChessboardSize = 6;

function load(CanvasX, CanvasY, ChessboardSize) {
	let c = document.getElementById('myC');
	let context = c.getContext('2d');
	c.height=c.height;  
	var HexWidthX = CanvasX * 2 / (7 * ChessboardSize);
	var HexWidthY = HexWidthX;
	var HexHeight = HexWidthX * 3 / 4;
	var StartX = CanvasX / 12,
		StartY = CanvasX / 12;
	var CoordinateX = StartX,
		CoordinateY = StartY;
	context.moveTo(CoordinateX, CoordinateY);
	for (var j = 0; j <= ChessboardSize; j++) {
		if (j == 0) {
			for (var i = 0; i < ChessboardSize; i++) {
				CoordinateX += HexWidthX;
				CoordinateY -= HexHeight;
				context.lineTo(CoordinateX, CoordinateY);
				CoordinateX += HexWidthX;
				CoordinateY += HexHeight;
				context.lineTo(CoordinateX, CoordinateY);
			}
			CoordinateX = StartX, CoordinateY = StartY + HexWidthY;
			context.moveTo(CoordinateX, CoordinateY);
		} else {
			for (var i = 0; i <= ChessboardSize; i++) {
				if (i != ChessboardSize || j != ChessboardSize) {
					CoordinateX += HexWidthX;
					CoordinateY += HexHeight;
					context.lineTo(CoordinateX, CoordinateY);
				}
				if (i != ChessboardSize) {
					CoordinateX += HexWidthX;
					CoordinateY -= HexHeight;
					context.lineTo(CoordinateX, CoordinateY);
				}
			}
			CoordinateX = StartX + HexWidthX * j, CoordinateY = StartY + HexWidthY + (HexWidthY + HexHeight) * j;
			context.moveTo(CoordinateX, CoordinateY);
		}
	}
	for (var i = 0; i < ChessboardSize; i++) {
		CoordinateX = StartX + HexWidthX * i;
		CoordinateY = StartY + (HexWidthY + HexHeight) * i;
		for (var j = 0; j <= ChessboardSize; j++) {
			context.moveTo(CoordinateX, CoordinateY);
			context.lineTo(CoordinateX, CoordinateY + HexWidthY);
			CoordinateX += HexWidthX * 2;
		}
	}
	context.stroke();
}

window.onload = function() {
	load(CanvasWidth, CanvasHeight, ChessboardSize);
}
