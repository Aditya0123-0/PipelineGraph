import { useMemo, useRef, useCallback } from "react";
import ForceGraph2D from "react-force-graph-2d";

// Mirrors the color mapping used in the README data model diagram, so the
// same node type always reads the same color across docs and live app.
const NODE_COLORS = {
  System: "#5B8DEF",
  Pipeline: "#F2795A",
  Workflow: "#9B7EDE",
  Agent: "#4FD1C5",
  DataEntity: "#F45B8D",
  Team: "#8891A5",
};

const IMPACT_COLOR = "#F5A623";

export default function GraphView({ nodes = [], edges = [], highlightedNodeIds = new Set(), onNodeClick }) {
  const fgRef = useRef();

  const graphData = useMemo(
    () => ({
      nodes: nodes.map((n) => ({ ...n })),
      links: edges.map((e) => ({ source: e.source, target: e.target, relationship: e.relationship })),
    }),
    [nodes, edges]
  );

  const paintNode = useCallback(
    (node, ctx, globalScale) => {
      const isHighlighted = highlightedNodeIds.has(node.id);
      const radius = isHighlighted ? 7 : 5;
      const color = NODE_COLORS[node.type] || "#8891A5";

      // Pulse ring for impacted nodes — the signature interaction moment.
      if (isHighlighted) {
        ctx.beginPath();
        ctx.arc(node.x, node.y, radius + 5, 0, 2 * Math.PI);
        ctx.strokeStyle = IMPACT_COLOR;
        ctx.lineWidth = 1.5;
        ctx.stroke();
      }

      ctx.beginPath();
      ctx.arc(node.x, node.y, radius, 0, 2 * Math.PI);
      ctx.fillStyle = isHighlighted ? IMPACT_COLOR : color;
      ctx.fill();

      const label = node.label || node.id;
      const fontSize = 11 / globalScale;
      ctx.font = `${fontSize}px "Inter", sans-serif`;
      ctx.textAlign = "center";
      ctx.fillStyle = "#E8ECF4";
      ctx.fillText(label, node.x, node.y + radius + fontSize + 2);
    },
    [highlightedNodeIds]
  );

  if (nodes.length === 0) {
    return (
      <div className="graph-view graph-view-empty">
        <p>No graph data to display yet.</p>
      </div>
    );
  }

  return (
    <div className="graph-view">
      <ForceGraph2D
        ref={fgRef}
        graphData={graphData}
        backgroundColor="#0B0E14"
        nodeCanvasObject={paintNode}
        nodePointerAreaPaint={(node, color, ctx) => {
          ctx.fillStyle = color;
          ctx.beginPath();
          ctx.arc(node.x, node.y, 8, 0, 2 * Math.PI);
          ctx.fill();
        }}
        linkColor={() => "#2A3242"}
        linkDirectionalArrowLength={4}
        linkDirectionalArrowRelPos={1}
        linkLabel={(link) => link.relationship}
        onNodeClick={(node) => onNodeClick && onNodeClick(node)}
        cooldownTicks={100}
      />
    </div>
  );
}