package edu.uma.nlp.graphseg.utils;

public class VectorOperations {
	public static double cosine(double[] vector, double[] otherVector) throws Exception
    {
        if (vector.length != otherVector.length) 
    	{
        	throw new UnsupportedOperationException("Vectors are of different length");
    	}
        
        double dp = 0;
        double sum1 = 0;
        double sum2 = 0;
        
        for (int i = 0; i < vector.length; i++)
        {
            dp += vector[i] * otherVector[i];
            sum1 += vector[i] * vector[i];
            sum2 += otherVector[i] * otherVector[i];
        }

        return dp / (Math.sqrt(sum1) * Math.sqrt(sum2));
    }
	
    public static void multiply(double[] vector, double factor)
    {
        for (int i = 0; i < vector.length; i++) vector[i] *= factor;
    }

    public double[] sumVectors(double[] vector, double[] otherVector)
    {
        if (vector.length != otherVector.length) throw new UnsupportedOperationException("Vectors are of different length");
        
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) result[i] = vector[i] += otherVector[i];
        
        return result;
    }
    
    public static void addVector(double[] vector, double[] otherVector)
    {
        if (vector.length != otherVector.length) throw new UnsupportedOperationException("Vectors are of different length");
        for (int i = 0; i < vector.length; i++) vector[i] += otherVector[i];
    }
}
