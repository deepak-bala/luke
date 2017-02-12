/**
 * 
 */
package org.apache.lucene.luke.core;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.LeafCollector;
import org.apache.lucene.search.Scorer;

import java.io.IOException;
import java.util.ArrayList;

class AllHitsCollector extends AccessibleHitCollector {
  private ArrayList<AllHit> hits = new ArrayList<AllHit>();

  public AllHitsCollector(boolean outOfOrder, boolean shouldScore) {
    this.outOfOrder = outOfOrder;
    this.shouldScore = shouldScore;
  }

  public void collect(int doc) {
    float score = 1.0f;
    if (shouldScore) {
      try {
        score = scorer.score();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    hits.add(new AllHit(docBase + doc, score));
  }

  public int getTotalHits() {
    return hits.size();
  }

  public int getDocId(int i) {
    return (hits.get(i)).docId;
  }

  public float getScore(int i) {
    return (hits.get(i)).score;
  }

  @Override
  public LeafCollector getLeafCollector(LeafReaderContext leafReaderContext) throws IOException {
    this.docBase = leafReaderContext.docBase;
    return new LeafCollector() {
      private Scorer scorer;
      @Override
      public void setScorer(Scorer scorer) throws IOException {
        this.scorer = scorer;
      }
      @Override
      public void collect(int doc) throws IOException {
        float score = 1.0f;
        if (shouldScore) {
          try {
            score = scorer.score();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
        hits.add(new AllHit(docBase + doc, score));
      }
    };
  }


  @Override
  public boolean needsScores() {
    return false;
  }

  private static class AllHit {
    public int docId;
    public float score;
    
    public AllHit(int docId, float score) {
      this.docId = docId;
      this.score = score;
    }
  }

  @Override
  public void reset() {
    hits.clear();
  }
}